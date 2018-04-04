package info.batey.akka.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future

object ServerApp extends App with UserRoute {

  implicit val system = ActorSystem("ServerApp")
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher

  //#bind
  val bound: Future[Http.ServerBinding] = Http().bindAndHandle(route, "localhost", 8080)
  //#bind

  bound.onComplete(println)
}
