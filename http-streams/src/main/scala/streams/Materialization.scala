package sc.streams

import akka.NotUsed
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.OverflowStrategy
import akka.stream.scaladsl._

import scala.concurrent.Future

class Materialization {
  def run() {
    //#multiple
    val graph = Source(0 to 20000000)
      .map(_.toString)
      .to(Sink.foreach(println))

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val n1: NotUsed = graph.run()
    val n2: NotUsed = graph.run()
    //#multiple

    //#source
    val source: Source[String, ActorRef]  =
      Source.actorRef(bufferSize = 23, OverflowStrategy.dropNew)

    val sink: Sink[String, Future[String]] =
      Sink.reduce(_ + _)

    val actor: ActorRef = source.to(sink).run()
    actor ! "Message"
    //#source
  }

  def sink() {
    val sources: scala.collection.immutable.Seq[Source[Int, NotUsed]] = ???
    val merged: Source[Int, NotUsed] = Source(sources).flatMapMerge(42, identity)
    //#sink
    val source: Source[String, ActorRef] =
     Source.actorRef(bufferSize = 23, OverflowStrategy.dropNew)

    val sink: Sink[String, Future[String]] =
     Sink.reduce(_ + _)

    val graph1: RunnableGraph[ActorRef] =
     source.to(sink)

    val graph2: RunnableGraph[Future[String]] =
     source.toMat(sink)(Keep.right)

    val graph3: RunnableGraph[(ActorRef, Future[String])] =
     source.toMat(sink)(Keep.both)
    //#sink

    //#fusing
     Source(1 to 3)
       .map(x => x + 1)
       .map(x => x * 2)
       .to(Sink.reduce[Int](_ + _))
    //#fusing

    //#fusing-explicit-async
     Source(1 to 3)
       .map(x => x + 1).async
       .map(x => x * 2)
       .to(Sink.reduce[Int](_ + _))
    //#fusing-explicit-async

    //#fusing-async
     Source(1 to 3)
       .map(x => x + 1)
       .mapAsync(5)(n => Future.successful(n * 2))
       .to(Sink.reduce[Int](_ + _))
    //#fusing-async
  }
}
