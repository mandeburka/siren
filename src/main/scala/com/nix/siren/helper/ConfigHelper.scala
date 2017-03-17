package com.nix.siren.helper

import akka.event.slf4j.Logger
import com.typesafe.config.ConfigFactory

import scala.util.Try

trait ConfigHelper {

  def config = ConfigHelper.config
  private def getProperty[T](name:String, f: String => T): Option[T] = Try(f.apply(name)).toOption match {
    case Some(p) => Some(p)
    case None =>
      ConfigHelper.logger.error(s"Property with name $name not found.")
      None
  }
  def getStringProperty(name: String) = getProperty(name, config.getString)
  def getIntProperty(name: String) = getProperty(name, config.getInt)
}

object ConfigHelper {

  private val logger = Logger(ConfigHelper.getClass.getName)

  val config: com.typesafe.config.Config = ConfigFactory.load()

}
