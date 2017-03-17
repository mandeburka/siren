package com.nix.siren.tcp

import java.net.InetSocketAddress

import akka.actor.{Actor, Props}
import akka.event.slf4j.Logger
import akka.io.{IO, Tcp}

class TCPServer(host: String, port: Int) extends Actor {

  import akka.io.Tcp._
  import context.system

  private[this] val logger = Logger(this.getClass.getName)

  IO(Tcp) ! Bind(self, new InetSocketAddress(host, port))

  override def receive: Receive = {
    case b @ Bound(localAddress) => logger.info(s"TCP Server was bounded to $localAddress ip address")
    case c @ Connected(remote, local) =>
      logger.debug(s"Creating new connection, remote address $remote, local address $local")
      val handler = context.actorOf(Props[ConnectionHandler])
      val connection = sender()
      connection ! Register(handler, keepOpenOnPeerClosed = true)
    case CommandFailed(_: Bind) => context stop self
  }
}
