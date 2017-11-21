package info.batey.akka.tcp

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util.concurrent.{Executors, TimeUnit}

import akka.actor.{ActorSystem, Cancellable}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent.duration._

object Client extends App {
  println("Client...")

  implicit val system = ActorSystem()
  implicit val materialiser = ActorMaterializer()
  implicit val ec = system.dispatcher

  val source: Source[ByteString, Cancellable] = Source.tick(10.millis, 10.millis,
    ByteString(new Array[Byte](10000)))
    .map(bs => {
      system.log.info("Sent")
      bs
    })
    .via(Tcp().outgoingConnection("localhost", 9090))

  source.runWith(Sink.foreach(println))
}


object StupidClient extends App {

  val system = ActorSystem()
  val socket: SocketChannel = SocketChannel.open()
  socket.connect(new InetSocketAddress("127.0.0.1", 9090))
  socket.socket().setTcpNoDelay(true)
  socket.configureBlocking(false)
  val es = Executors.newScheduledThreadPool(1)
  es.scheduleAtFixedRate(() => {
    val bb: ByteBuffer = ByteBuffer.allocate(10000)
    bb.put(new Array[Byte](10000))
    bb.flip()
    socket.write(bb)
    system.log.info("Sent")
  }, 10L, 10L, TimeUnit.MILLISECONDS)

}
