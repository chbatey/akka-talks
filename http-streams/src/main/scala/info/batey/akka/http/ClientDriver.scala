package info.batey.akka.http

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.stream.testkit.TestSubscriber
import akka.stream.testkit.scaladsl.TestSink
import com.typesafe.config.ConfigFactory
import info.batey.akka.http.Domain.Event

import scala.io.StdIn
import scala.util.{Failure, Success, Try}

object ClientDriver extends App with ActivityClient {

  val config = ConfigFactory.parseString(
    """
      |akka.log-level = DEBUG
      |akka.http.client.idle-timeout = 30m
      |akka.http.client.parsing.max-content-length = infinite
      |akka.http.host-connection-pool.client.idle-timeout = infinite
    """.stripMargin)

  implicit val system = ActorSystem("ClientDriver", config)
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher

  val activity = eventsForUser("chbatey")

  val testProbe = TestSink.probe[Event]

  activity.onComplete {
    case Success(source) =>
      val sub: TestSubscriber.Probe[Event] = source.runWith(testProbe)
      while (true) {
        Try {
          val request = StdIn.readLine().toInt
          println(s"Requesting " + request)
          sub.request(request)
          val results = sub.expectNextN(request)
          println(results)
        }
      }
    case Failure(t) =>
      println("Darn" + t)
  }
}
