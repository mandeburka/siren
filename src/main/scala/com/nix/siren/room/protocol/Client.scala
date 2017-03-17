package com.nix.siren.room.protocol

import java.util.UUID

import akka.actor.ActorRef

final case class Client(id: UUID, tags: Set[String], handle: ActorRef)