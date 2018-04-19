package info.batey.akka

import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.stream.scaladsl.Source
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

  val service: HttpRequest => Future[HttpResponse] = GreeterServiceHandler(new GreeterServiceImpl(mat))
    .orElse { case _ => Future.successful(HttpResponse(StatusCodes.NotFound)) }

  Http().bindAndHandleAsync(service, interface = "127.0.0.1", port = 8080)
    .foreach { binding =>
      println(s"gRPC server bound to: ${binding.localAddress}")
    }
}

object ClientApp extends App {
  implicit val sys: ActorSystem = ActorSystem("HelloWorld")
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = sys.dispatcher

  val client = new GreeterServiceClient(new GrpcClientSettings(
    "127.0.0.1",
    8080
  ))

  println("unary")
  client.sayHello(HelloRequest("chbatey"))

  println("")
  client.itKeepsTalking(Source(List(HelloRequest("chbatey"), HelloRequest("trevor"))))


}
