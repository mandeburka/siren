package com.nix.siren.room.protocol

import java.util.UUID

import com.nix.siren.room.protocol.client.{Registered, Unregistered}


sealed trait Command
final case class Connect(client: Unregistered) extends Command
final case class Disconnect(clientId: UUID) extends Command
final case class Validate(messageId: UUID)

sealed trait Message extends Command {
  def body: String
  def from: UUID
}

final case class Broadcast(body: String, from: UUID) extends Message
final case class Direct(body: String, from: UUID, recipientIds: Iterable[UUID]) extends Message

sealed trait CommandResponse
case object Connected extends CommandResponse
case object OK extends CommandResponse