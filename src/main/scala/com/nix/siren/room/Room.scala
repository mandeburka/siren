package com.nix.siren.room


import java.util.UUID

import akka.actor.{Actor, Props}
import com.nix.siren.room.protocol._

import scala.collection.mutable

class Room() extends Actor {
  private val sessions = mutable.Map.empty[UUID, Client]

  def receive: Actor.Receive = {
    case Connect(client) =>
      sessions += client.id -> client
      notify(sessions.values.filterNot(_.id == client.id), ClientConnected(client))
      sender() ! OK
    case Disconnect(client) =>
      sessions -= client.id
      sender() ! OK
      notify(sessions.values, ClientDisconnected(client))
    case message: SendMessage =>
      postMessage(message)
      sender() ! OK
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

  private def notify(recipients: Iterable[Client], event: Event) = {
    recipients.foreach(_.handle ! event)
  }
}

object Room {
  def props: Props = Props[Room]
}
