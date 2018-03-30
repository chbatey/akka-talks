package sc.streams

import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source, Tcp}
import akka.stream.{ActorMaterializer, ThrottleMode}
import akka.util.ByteString

object TcpServer extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  val bind = Tcp(system).bind("127.0.0.1", 8081)
  bind.runForeach(conn => {
    println("Got a connection!")
    val sink = Sink.ignore
    val source = Source(0 to 200000000)
      .throttle(5, per = 100.milliseconds, maximumBurst = 10, ThrottleMode.shaping)
      .map(i => ByteString.fromString(i.toString + "\n"))
    conn.handleWith(Flow.fromSinkAndSourceCoupled(sink, source))
  })
}
