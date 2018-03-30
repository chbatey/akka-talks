package sc.http

import scala.concurrent.Future

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.IncomingConnection
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source

class Basics {
  def run() = {
    //#init
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    val http = Http()
    //#init

    //#bind
    val binding: Source[IncomingConnection, Future[ServerBinding]] =
      http.bind("localhost", port = 0)
    //#bind

    //#run

    val bound: Future[ServerBinding] = binding
      .to(Sink.foreach(c => println(s"Got HTTP connection!")))
      .run()
    //#run

    implicit val ec = materializer.executionContext
    //#log
    bound.foreach(b =>
      println("Bound on " + b.localAddress))
    //#log
  }

  def flow() = {
    val connection: IncomingConnection = null
    //#flow
    val flow: Flow[HttpResponse, HttpRequest, NotUsed] =
      connection.flow

    // Materialize and run it yourself
    //#flow
  }
  def handleWith() = {
    val connection: IncomingConnection = null
    implicit val materializer: Materializer  = null
    val myflow: Flow[HttpRequest, HttpResponse, NotUsed] = null
    //#handleWith
    // Construct your flow:
    val flow: Flow[HttpRequest, HttpResponse, NotUsed] = myflow

    // Use it to handle the connection:
    connection.handleWith(flow)
    //#handleWith
  }
  def bindAndHandle() {
    implicit val system = ActorSystem()
    implicit val materializer: Materializer = null
    val myflow: Flow[HttpRequest, HttpResponse, NotUsed] = null
    val http = Http()
    //#bindAndHandle
    // Construct your flow:
    val flow: Flow[HttpRequest, HttpResponse, NotUsed] = myflow

    // Use it to handle connections:
    http.bindAndHandle(
      flow,
      "localhost",
      port = 8080)
    //#bindAndHandle
  }
}
