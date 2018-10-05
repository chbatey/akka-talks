package info.batey.akka.streams

/*
import akka.NotUsed
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.stream.SourceRef
import akka.stream.scaladsl._
import akka.stream.typed._
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import info.batey.akka.streams.Server.{GetStream, ServerCommand, StreamEnvelope}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Q: Why just in Akka cluster?
  */

object MulticaseServerApp extends App {

  val config = ConfigFactory.parseString(
    """
      akka.remote.netty.tcp.port = 2551
    """).withFallback(ConfigFactory.load())

  val as = ActorSystem(Server.bahavior, "system", config)

}

object MulticaseClientApp extends App {

  val config = ConfigFactory.parseString(
    """
      akka.remote.netty.tcp.port = 2552
    """).withFallback(ConfigFactory.load())

  val as = ActorSystem(Client.behavior, "system", config)
}

object Something extends App {

  implicit val as = akka.actor.ActorSystem()
  implicit val mat = akka.stream.ActorMaterializer()
  implicit val ec = as.dispatcher

  val broadcastHub: Sink[String, Source[String, NotUsed]] = BroadcastHub.sink[String]

  val initialSource = Source.tick(1.second, 1.second, "go")

  val funkySource: Source[String, NotUsed] = initialSource.toMat(broadcastHub)(Keep.right).run()

  val y: Future[SourceRef[String]] = funkySource.runWith(StreamRefs.sourceRef())

  y.onComplete {
    case Success(sr) => sr.runWith(Sink.foreach(m => println("y " + m)))
    case Failure(t) => println(t)
  }

 val x: Future[SourceRef[String]] = funkySource.runWith(StreamRefs.sourceRef())

  x.onComplete {
    case Success(sr) => sr.runWith(Sink.foreach(m => println("x " + m)))
    case Failure(t) => println(t)
  }


}


object MulticaseClient {

  sealed trait Protocol
  case class RemoteReady(actorRef: ActorRef[ServerCommand]) extends Protocol
  case object RemoteNotReady extends Protocol
  case class RemoteUnavailable(t: Throwable) extends Protocol
  case class SourceRefReady(sourceRef: SourceRef[Int]) extends Protocol
  case object StreamFinished extends Protocol

  val behavior = Behaviors.setup[Protocol] { ctx =>

    implicit val materializer = ActorMaterializer()(ctx.system)
    implicit val ec = ctx.executionContext
    implicit val timeout = Timeout(1.second)

    val receptionistAdapter = ctx.messageAdapter[Receptionist.Listing] {
      case listing: Receptionist.Listing if listing.getServiceInstances(Server.key).size() > 0 =>
        println("Got listing")
        RemoteReady(listing.getServiceInstances(Server.key).iterator().next())
      case _: Receptionist.Listing =>
        println("No listings yet")
        RemoteNotReady
    }

    val streamEnvelopeAdapter: ActorRef[StreamEnvelope] = ctx.messageAdapter {
      case StreamEnvelope(ref) => SourceRefReady(ref)
    }

    val awaitingStreamFinished = Behaviors.receiveMessagePartial[Protocol] {
      case StreamFinished =>
        println("Stream finished")
        Behaviors.same
    }

    val awaitingStreamRef = Behaviors.receivePartial[Protocol] {
      case (ctx, SourceRefReady(streamRef)) =>
        streamRef.runWith(Sink.foreach(println)).onComplete { tr =>
          println(tr)
          ctx.self ! StreamFinished
        }
        awaitingStreamFinished
    }

    val awaitingRemote = Behaviors.receive[Protocol] { (ctx, msg) =>
      msg match {
        case RemoteReady(ref) =>
          ref ! GetStream(streamEnvelopeAdapter)
          awaitingStreamRef
        case RemoteUnavailable(t) =>
          println("Unable unavailable: " + t)
          Behaviors.stopped
        case RemoteNotReady =>
          println("Still waiting on listing")
          Behaviors.same
      }
    }

    ctx.system.receptionist ! Receptionist.Subscribe(Server.key, receptionistAdapter)

    awaitingRemote
  }


}

object MulicaseServer {

  val key = ServiceKey[ServerCommand]("server")

  sealed trait ServerCommand
  case class GetStream(replyTo: ActorRef[StreamEnvelope]) extends ServerCommand
  case class StreamReady(streamRef: SourceRef[Int], replyTo: ActorRef[StreamEnvelope]) extends ServerCommand

  case class StreamEnvelope(streamRef: SourceRef[Int])

  val behavior = Behaviors.setup[ServerCommand] { ctx =>

    implicit val materializer = ActorMaterializer()(ctx.system)
    implicit val ec = ctx.executionContext

    ctx.system.receptionist ! Receptionist.Register(key, ctx.self)

    Behaviors.receive[ServerCommand] { (ctx, cmd) =>
      cmd match {
        case GetStream(replyTo) =>
          val source = Source(1 to 100)
          //Q: Why is this a future?
          val sourceRef: Future[SourceRef[Int]] = source.runWith(StreamRefs.sourceRef())
          sourceRef.onComplete {
            case Success(ref) => ctx.self ! StreamReady(ref, replyTo)
            case Failure(t) => println("OH dear: " + t)
          }
          Behaviors.same
        case StreamReady(streamRef, replyTo) =>
          replyTo ! StreamEnvelope(streamRef)
          Behaviors.same
      }
    }
  }
}
*/
