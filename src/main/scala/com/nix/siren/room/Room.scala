package com.nix.siren.room


import java.util.UUID

import akka.actor.{Actor, Props}
import com.nix.siren.room.protocol._
import com.nix.siren.room.protocol.client.{Registered, Role, Unregistered}

import scala.collection.mutable

class Room() extends Actor {
  private val sessions = mutable.Map.empty[UUID, Registered]
  private var master: Option[Registered] = None

  def receive: Actor.Receive = {
    case Connect(client) =>
      sender() ! connect(client)
    case Disconnect(client) =>
      sessions -= client.id
      notify(sessions.values, ClientDisconnected(client))
      sender() ! OK
    case message: SendMessage =>
      postMessage(message)
      sender() ! OK
  }

  private def connect(client: Unregistered) = {
    val role = if (master.isDefined) Role.Guest else Role.Master
    val registeredClient = Registered(
      id = client.id,
      handle = client.handle,
      tags = client.tags,
      role = role
    )
    sessions += client.id -> registeredClient
    if (master.isEmpty) master = Some(registeredClient)
    notify(sessions.values.filterNot(_.id == client.id), ClientConnected(registeredClient))
    Connected(registeredClient)
  }

  private def postMessage(message: SendMessage) = {
    val recipients = message match {
      case msg: Direct ⇒
        sessions.filterKeys(_ == msg.recipientId)
      case _: Broadcast ⇒
        sessions
      case msg: MatchAll ⇒
        sessions.filter {
          case (_, client) => client.tags.intersect(msg.tags) == msg.tags
        }
      case msg: MatchAny ⇒
        sessions.filter {
          case (_, client) => client.tags.intersect(msg.tags).nonEmpty
        }
    }
    notify(
      recipients.values.filterNot(_.id == message.from.id),
      MessageSent(message.body, message.from)
    )
  }

  private def notify(recipients: Iterable[Registered], event: Event) = {
    recipients.foreach(_.handle ! event)
  }
}

object Room {
  def props: Props = Props[Room]
}
