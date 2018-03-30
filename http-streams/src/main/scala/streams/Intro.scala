package sc.streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

class Intro {
  //#source
  val source = Source(0 to 20000000)
  //#source
  //#flow
  val flow = Flow[Int].map(_.toString())
  //#flow
  //#sink
  val sink = Sink.foreach[String](println(_))
  //#sink
  //#graph
  val runnable = source.via(flow).to(sink)
  //#graph
  //#run
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  runnable.run()
  //#run

  //#short
  Source(0 to 20000000)
    .map(_.toString)
    .runForeach(println)
  //#short
}
