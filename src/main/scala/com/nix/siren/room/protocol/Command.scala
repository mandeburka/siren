package com.nix.siren.room.protocol

import java.util.UUID

import com.nix.siren.room.protocol.client.{Registered, Unregistered}


sealed trait Command
final case class Connect(client: Unregistered) extends Command
final case class Disconnect(client: Registered) extends Command
final case class Validate(messageId: UUID)

sealed trait Message extends Command {
  def body: String
  def from: Registered
}

final case class Broadcast(body: String, from: Registered) extends Message
final case class Direct(body: String, from: Registered, recipientIds: Iterable[UUID]) extends Message

sealed trait CommandResponse
final case class Connected(client: Registered) extends CommandResponse
case object OK extends CommandResponse