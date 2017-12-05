package info.batey.akka.tcp

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, OverflowStrategy, ThrottleMode}
import akka.stream.scaladsl.{Tcp, _}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._

import scala.util.{Failure, Success}

object Server extends App {
  val config = ConfigFactory.parseString(
    """
      |akka.loglevel = DEBUG
    """.stripMargin)

  println("Server...")
  implicit val system = ActorSystem("Server", config)
  implicit val materialiser = ActorMaterializer()
  implicit val ec = system.dispatcher

  val processingFlow =
    Chunker(chunkSize = 2)
      .throttle(1, 2.seconds, 1, ThrottleMode.Shaping)
      .via(
        Flow.fromFunction((bs: ByteString) => {
          system.log.info("Bs:" + bs)
          ByteString()
        }))

  val binding = Tcp().bind("localhost", 9090)

  val echoServer = binding.map(ic => ic.handleWith(processingFlow))

  val bound = echoServer.to(Sink.ignore).run()

  bound.onComplete {
    case Success(b) =>
      println("Successfully bound: " + b)
    case Failure(t) =>
      println("Failed to bind: " + t)
      system.terminate()
  }
}


