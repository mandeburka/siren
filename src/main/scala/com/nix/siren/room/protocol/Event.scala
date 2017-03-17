package com.nix.siren.room.protocol

import com.nix.siren.room.protocol.client.Registered

sealed trait Event
final case class MessageSent(message: String, from: Registered) extends Event
final case class ClientConnected(client: Registered) extends Event
final case class ClientDisconnected(client: Registered) extends Event
