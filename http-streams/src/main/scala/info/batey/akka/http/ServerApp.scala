package info.batey.akka.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future

object ServerApp extends App with UserRoute {

  val config = ConfigFactory.parseString(
    """
      |akka.log-level = DEBUG
      |akka.http.client.idle-timeout = infinite
      |akka.http.server.idle-timeout = infinite
      |akka.http.client.parsing.max-content-length = infinite
      |akka.http.host-connection-pool.client.idle-timeout = infinite
    """.stripMargin)

  implicit val system = ActorSystem("ServerApp", config)
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher

  val bound: Future[Http.ServerBinding] = Http().bindAndHandle(route, "localhost", 8080)
  bound.onComplete(println)
}
