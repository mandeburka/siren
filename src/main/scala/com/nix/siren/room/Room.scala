package com.nix.siren.room


import java.util.UUID

import akka.actor.{Actor, Props, Status}
import com.nix.siren.room.protocol._
import com.nix.siren.room.protocol.client.{Registered, Role, Unregistered}

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

class Room(masterValidation: Boolean) extends Actor {

  private val sessions = mutable.Map.empty[UUID, Registered]
  private var master: Option[Registered] = None
  private val pendingValidation = mutable.Map.empty[UUID, Message]

  def receive: Actor.Receive = {
    case Connect(client) =>
      sender() ! connect(client)
    case Disconnect(clientId) =>
      validateClient(clientId) match {
        case Success(client) =>
          sessions -= client.id
          notify(sessions.values, ClientDisconnected(client))
          sender() ! OK
        case Failure(exception) =>
          sender() ! Status.Failure(exception)
      }
    case message: Message =>
      processMessage(message)
      sender() ! OK
    case Validate(messageId) =>
      validate(messageId)
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
    Connected
  }

  private def postMessage(message: Message) = {
    val recipients: Iterable[Registered] = message match {
      case msg: Direct ⇒
        msg.recipientIds.foldLeft(List.empty[Registered]) {
          case (clients, id) => clients ++ sessions.get(id)
        }
      case _: Broadcast ⇒
        sessions.values
    }
    notify(
      recipients.filterNot(_.id == message.from),
      MessageSent(message.body, message.from)
    )
  }

  private def notify(recipients: Iterable[Registered], event: Event) = {
    recipients.foreach(_.handle ! event)
  }

  private def processMessage(message: Message) = {
    if (masterValidation && master.isDefined) {
      val needValidation = NeedValidation(id = UUID.randomUUID(), message = message)
      pendingValidation += needValidation.id -> needValidation.message
      master.foreach(_.handle ! needValidation)
    } else postMessage(message)
  }

  private def validate(messageId: UUID) = {
    pendingValidation.get(messageId) match {
      case Some(message) =>
        pendingValidation -= messageId
        postMessage(message)
      case None =>
    }
  }

  private def validateClient(clientId: UUID): Try[Registered] = {
    sessions.get(clientId) match {
      case Some(client) => Success(client)
      case None => Failure(new Exception(s"Client $clientId is not found"))
    }
  }
}

object Room {
  def props(masterValidation: Boolean): Props = Props(classOf[Room], masterValidation)
}
