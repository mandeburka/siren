package com.nix.siren.room.protocol

import java.util.UUID


sealed trait Command
final case class Connect(client: Client) extends Command
final case class Disconnect(client: Client) extends Command

sealed trait SendMessage extends Command {
  def body: String
  def from: Client
}

final case class Broadcast(body: String, from: Client) extends SendMessage
final case class Direct(body: String, from: Client, recipientId: UUID) extends SendMessage
final case class MatchAny(body: String, from: Client, tags: Set[String]) extends SendMessage
final case class MatchAll(body: String, from: Client, tags: Set[String]) extends SendMessage

sealed trait CommandResponse
case object OK extends CommandResponse