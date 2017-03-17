package com.nix.siren.room.protocol

import java.util.UUID

import com.nix.siren.room.protocol.client.Registered

sealed trait Event
final case class MessageSent(message: String, from: UUID) extends Event
final case class ClientConnected(client: Registered) extends Event
final case class ClientDisconnected(client: Registered) extends Event
final case class NeedValidation(id: UUID, message: Message)
