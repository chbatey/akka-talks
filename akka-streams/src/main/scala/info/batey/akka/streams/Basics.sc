import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

implicit val system = ActorSystem()
implicit val materialiser = ActorMaterializer()

val source: Source[Int, NotUsed] = Source(1 to 100)
val foldingSink: Sink[Int, Future[Int]] = Sink.fold(0)(_ + _)
val ran: Future[Int] = source.runWith(foldingSink)
Await.result(run, Duration.Inf)

val printingSink: Sink[Any, Future[Done]] = Sink.foreach(println)
val runPrintingSink: Future[Done] = source.runWith(printingSink)
Await.ready(runPrintingSink, Duration.Inf)

val runAgain: Future[Int] = source.map(_ * 100).runWith(foldingSink)
Await.result(runAgain, Duration.Inf)

val flow: Flow[Int, Int, NotUsed] = Flow[Int]
  .filter(_ % 2 == 0)
  .map(_ * 10)

val viaAFlow: Source[Int, NotUsed] = source.via(flow)

Await.result(source.runWith(foldingSink), Duration.Inf)
