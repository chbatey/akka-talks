package sc.http

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.{Directives, Route, RouteResult}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow

object Routing extends Directives {
  implicit val system = ActorSystem()
  val http = Http()
  implicit val materializer = ActorMaterializer()
  //#simple
  val route: Route = path("hello") {
    get {
      complete("Hello, world!")
    }
  }
  //#simple
  //#run
  // RouteResult.route2HandlerFlow(route)
  val flow2: Flow[HttpRequest, HttpResponse, NotUsed] = route
  //#run
}