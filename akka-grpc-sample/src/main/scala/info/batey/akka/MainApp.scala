package info.batey.akka

import java.time.Instant
import java.util.UUID

import akka.{Done, NotUsed}
import akka.actor.{ActorSystem, Cancellable}
import akka.grpc.GrpcClientSettings
import akka.http.scaladsl.UseHttp2.Always
import akka.http.scaladsl.{Http2, HttpConnectionContext}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.ConfigFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

object MainApp extends App {

  // important to enable HTTP/2 in ActorSystem's config
  val conf = ConfigFactory.parseString(
    """
       akka.loglevel = DEBUG
      akka.http.server.preview.enable-http2 = on
    """)
    .withFallback(ConfigFactory.defaultApplication())
  implicit val sys: ActorSystem = ActorSystem("HelloWorld", conf)
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = sys.dispatcher

  //#service
  val service: HttpRequest => Future[HttpResponse] =
    GreeterServiceHandler(new GreeterServiceImpl(mat))
  //#service

  //#binding
  Http2().bindAndHandleAsync(service, "127.0.0.1", 9001, HttpConnectionContext(http2 = Always))
    .foreach { binding =>
      println(s"gRPC server bound to: ${binding.localAddress}")
    }
  //#binding
}

object ClientApp extends App {
  val conf = ConfigFactory.parseString(
    """
       akka.loglevel = DEBUG
    """.stripMargin)
  implicit val sys: ActorSystem = ActorSystem("HelloWorld", conf)
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = sys.dispatcher

  //#client
  val settings = GrpcClientSettings.connectToServiceAt("127.0.0.1", 9001)
  println(settings)
  val client = new GreeterServiceClient(settings)
  //#client

  //#unary
//  val response: Future[HelloReply] =
//    client.sayHello(HelloRequest("chbatey"))
  //#unary
//  response.onComplete(println)

  //#client-streaming
//  val clientStreaming: Future[HelloReply] =
//    client.itKeepsTalking(Source(List(
//      HelloRequest("chbatey"),
//      HelloRequest("trevor"))))
  //#client-streaming
//  clientStreaming.onComplete(println)

  val slowSource: Source[HelloRequest, NotUsed] = Source.tick(1.second, 1.second, HelloRequest("Chris")).mapMaterializedValue(_ => NotUsed)

  //#streaming-both
  println("Starting: " + Instant.now())
  val streamedResponse: Source[HelloReply, NotUsed] =
    client.streamHellos(slowSource)
  val x: Future[Done] = streamedResponse.runWith(Sink.foreach(println))
  //#streaming-both

  client.closed().onComplete { s => println("Client has closed down: " + s + " " + Instant.now()) }

}
