package info.batey.akka.http

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.stream.testkit.TestSubscriber
import akka.stream.testkit.scaladsl.TestSink
import com.typesafe.config.ConfigFactory
import info.batey.akka.http.Domain.Event

import scala.concurrent.Future
import scala.io.StdIn
import scala.util.{Failure, Success, Try}

object ClientDriver extends App with ActivityClient {

  implicit val system = ActorSystem("ClientDriver")
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher

  // Why is it a Future?
  // Why is it a Source?
  // This is one HTTP request
  val activity: Future[Source[Event, NotUsed]] = eventsForUser("chbatey")

  // A "Test" Sink where you control the demand
  val testProbe = TestSink.probe[Event]

  // Reminder: Wireshark/Mission control/ss

  activity.onComplete {
    case Success(source) =>
      println("Connected to HTTP server. Please enter initial demand")
      val sub: TestSubscriber.Probe[Event] = source.runWith(testProbe)
      while (true) {
        Try {
          val request = StdIn.readLine().toInt
          println(s"Requesting " + request)
          (0 until request).foreach { _ =>
            println(sub.requestNext())
          }
          println(s"Successfully read $request events. Enter how many more...")
        }
      }
    case Failure(t) =>
      println(s"Unable to connect to server. Did you start it Christopher??? ${t.getMessage}")
  }
}
