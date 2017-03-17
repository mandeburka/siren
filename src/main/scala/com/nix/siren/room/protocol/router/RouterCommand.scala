package com.nix.siren.room.protocol.router

sealed trait RouterCommand

case class CreateRoom(tags: Map[String, String]) extends RouterCommand
case class ReconnectToRoom(roomId: String, clientId: String) extends RouterCommand
case class FindRoom(roomId: String, createIfNotExists: Boolean = true) extends RouterCommand
