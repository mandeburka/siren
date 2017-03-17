package com.nix.siren.room.protocol

sealed trait Event
final case class MessageSent(message: String, from: Client) extends Event
final case class ClientConnected(client: Client) extends Event
final case class ClientDisconnected(client: Client) extends Event
