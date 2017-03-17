package com.nix.siren.tcp.protocol

sealed trait ConnectionCommand
case class Connect(roomId: Option[String], tags: Map[String, String]) extends ConnectionCommand
case class Reconnect(clientId: String, roomId: String) extends ConnectionCommand



