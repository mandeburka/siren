package com.nix.siren.room.protocol.client

import java.util.UUID

import akka.actor.ActorRef

case class Unregistered(id: UUID, handle: ActorRef, tags: Set[String]) extends Base
