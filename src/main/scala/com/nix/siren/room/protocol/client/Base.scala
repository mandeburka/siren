package com.nix.siren.room.protocol.client

import java.util.UUID

import akka.actor.ActorRef

trait Base {
  def id: UUID
  def handle: ActorRef
  def tags: Set[String]
}
