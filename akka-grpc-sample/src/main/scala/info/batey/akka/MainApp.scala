package info.batey.akka

import akka.NotUsed
import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import akka.http.scaladsl.UseHttp2.Always
import akka.http.scaladsl.{ConnectionContext, Http, Http2, HttpConnectionContext}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.ConfigFactory

import scala.concurrent.{ExecutionContext, Future}

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
    .orElse { case _ => Future.successful(HttpResponse(StatusCodes.NotFound)) }

  //#binding
  Http2().bindAndHandleAsync(service, "127.0.0.1", 8080, HttpConnectionContext(http2 = Always))
    .foreach { binding =>
      println(s"gRPC server bound to: ${binding.localAddress}")
    }
  //#binding
}

object ClientApp extends App {
  implicit val sys: ActorSystem = ActorSystem("HelloWorld")
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = sys.dispatcher

  //#client
  val client = new GreeterServiceClient(new GrpcClientSettings(
    "127.0.0.1",
    8080
  ))
  //#client

  //#unary
  val response: Future[HelloReply] =
    client.sayHello(HelloRequest("chbatey"))
  //#unary
  response.onComplete(println)

  //#client-streaming
  val clientStreaming: Future[HelloReply] =
    client.itKeepsTalking(Source(List(
      HelloRequest("chbatey"),
      HelloRequest("trevor"))))
  //#client-streaming
  clientStreaming.onComplete(println)

  //#streaming-both
  val streamedResponse: Source[HelloReply, NotUsed] =
    client.streamHellos(???)
  streamedResponse.runWith(Sink.foreach(println))
  //#streaming-both
}
