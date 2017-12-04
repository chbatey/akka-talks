package info.batey.akka.http

import java.nio.charset.StandardCharsets

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.datastax.driver.core.Session
import info.batey.akka.http.Domain.User
import spray.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

trait UserRoute {
  import JsonProtocol._

  def route(implicit mat: ActorMaterializer): Route =
    path("user" / "tracking" / Segment) { name: String =>

      val source = DataAccess.lookupEvents(name).map(e =>
        ByteString(s"${e.toJson.toString()}\n", StandardCharsets.UTF_8))

      complete(HttpEntity(ContentTypes.`application/json`, source))
    } ~
      path("user" / Segment) { name: String =>
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
}
