package com.nix.siren.tcp

import akka.actor.{Actor, ActorPath, Terminated}
import akka.event.slf4j.Logger
import akka.io.Tcp
import akka.io.Tcp.Received
import com.nix.siren.helper.ConfigHelper
import com.nix.siren.tcp.protocol.{Connect, ConnectionCommand, Reconnect}

class ConnectionHandler extends Actor with ConfigHelper{

  private[this] val logger = Logger(this.getClass.getName)
  private[this] val roomRouter = context.actorSelection(ActorPath.fromString(""))

  override def receive: Receive = {
    case Received(data) => // parsing data
    case c: ConnectionCommand => authorisation.apply(c)
    case c: Tcp.ConnectionClosed =>
      if(c.isErrorClosed) logger.debug(s"Error closed connection: ${c.getErrorCause}")
      context stop self
  }

  def authorisation: PartialFunction[ConnectionCommand, Unit] = {
    case Reconnect(clientId, roomId) => //reconnecting to created room
    case Connect(roomId, tags) => roomId match {
      case Some(rid) =>
      case None =>
    }
  }
}
