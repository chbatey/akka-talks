package info.batey.akka.http

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Framing, Source}
import akka.util.ByteString
import info.batey.akka.http.Domain.Event
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

object ActivityClient {
  import JsonProtocol._

  def eventsForUser(userId: String)(implicit system: ActorSystem, mat: ActorMaterializer, ec: ExecutionContext): Future[Source[Event, NotUsed]] = {
    val response: Future[HttpResponse] = Http().singleRequest(
      HttpRequest(uri = s"http://localhost:8080/user/tracking/$userId")
    )
    response.map {
      case HttpResponse(StatusCodes.OK, headers, entity, _) =>
        entity.dataBytes.via(Framing.delimiter(
          ByteString("\n"), maximumFrameLength = 100, allowTruncation = true))
          .map(_.utf8String.parseJson.convertTo[Event])
          .mapMaterializedValue(_ => NotUsed)
    }
  }
}
