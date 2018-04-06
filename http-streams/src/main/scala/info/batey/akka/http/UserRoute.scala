package info.batey.akka.http

import java.nio.charset.StandardCharsets

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import info.batey.akka.http.Domain.{Event, User}
import spray.json._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

trait UserRoute {

  import JsonProtocol._

  implicit val system: ActorSystem
  implicit val mat: ActorMaterializer

  val userRouteNoCass =
    path("user-no-cass" / Segment) { name =>
      get {
        withRequestTimeout(500.millis) {
          val user: Future[Option[User]] = DataAccess.loopupUserStub(name)
          onComplete(user) {
            case Success(None) => complete(StatusCodes.NotFound)
            case Success(Some(u)) => complete(u)
            case Failure(t) => complete(StatusCodes.InternalServerError, t.getMessage)
          }
        }
      }
    }

   val userRouteBlocking =
    path("user-blocking" / Segment) { name =>
      get {
        withRequestTimeout(500.millis) {
          val user: Future[Option[User]] = DataAccess.loopupUserBlocking(name)
          onComplete(user) {
            case Success(None) => complete(StatusCodes.NotFound)
            case Success(Some(u)) => complete(u)
            case Failure(t) => complete(StatusCodes.InternalServerError, t.getMessage)
          }
        }
      }
    }


  /**
    * Small request all async from incoming TCP socket to outgoing TCP socket
    * to the database and then back again.
    *
    * Demonstrates: Async == Scalability
    */
  val userRoute =
  //#user-route
    path("user" / Segment) { name =>
      get {
        withRequestTimeout(500.millis) {
          val user: Future[Option[User]] = DataAccess.lookupUser(name)
          onComplete(user) {
            case Success(None) => complete(StatusCodes.NotFound)
            case Success(Some(u)) => complete(u)
            case Failure(t) => complete(StatusCodes.InternalServerError, t.getMessage)
          }
        }
      }
    }
  //#user-route

  /**
    * Unbounded request. Might bring back large quantities from the database.
    *
    * Demonstrates: Flow control == No wasted work, constant memory footprint which enables scalability.
    */
  //#stream-route
  val streamingRoute = path("user" / "tracking" / Segment) { name: String =>
    val source: Source[Event, NotUsed] =
      DataAccess.lookupEvents(name)
    val asJson: Source[ByteString, NotUsed] = source.map(e =>
      ByteString(s"${e.toJson.toString()}\n",
        StandardCharsets.UTF_8))

    complete(HttpEntity(ContentTypes.`application/json`, asJson))
  }
  //#stream-route

  val route: Route = streamingRoute ~ userRoute ~ userRouteNoCass ~ userRouteBlocking
}
