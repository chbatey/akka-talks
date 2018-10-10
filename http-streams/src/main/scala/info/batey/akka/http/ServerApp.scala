package info.batey.akka.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.concurrent.Future

// Run with a small heap
object ServerApp extends App with UserRoute {

  implicit val system = ActorSystem("ServerApp")
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher

  //#bind
  val bound: Future[Http.ServerBinding] = Http().
    bindAndHandle(route, "localhost", 8080)
  //#bind

  bound.onComplete(bound => {
    println("Server up and running. Go forth and demo the flow control... " + bound)
  })
}
