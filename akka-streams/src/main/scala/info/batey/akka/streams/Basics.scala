package info.batey.akka.streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

object Basics {

  implicit val actor = ActorSystem()
  implicit val materialiser = ActorMaterializer()

  Source(List(1,2,3))

  Source(List(1,2,3))


}
