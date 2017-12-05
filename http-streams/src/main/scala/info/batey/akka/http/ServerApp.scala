package info.batey.akka.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.datastax.driver.core.Cluster

import scala.concurrent.Future

object ServerApp extends App with UserRoute {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher

  val bound: Future[Http.ServerBinding] = Http().bindAndHandle(route, "localhost", 8080)
  bound.onComplete(println)
}
