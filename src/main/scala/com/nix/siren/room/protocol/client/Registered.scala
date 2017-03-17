package com.nix.siren.room.protocol.client

import java.util.UUID

import akka.actor.ActorRef

case class Registered(id: UUID, handle: ActorRef, tags: Set[String], role: Role.Value) extends Base
