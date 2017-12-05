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

trait ActivityClient {

  import JsonProtocol._

  implicit val system: ActorSystem
  implicit val mat: ActorMaterializer
  implicit val ec: ExecutionContext

  def eventsForUser(userId: String): Future[Source[Event, NotUsed]] = {

    val response: Future[HttpResponse] = Http().singleRequest(
      HttpRequest(uri = s"http://localhost:8080/user/tracking/$userId")
    )

    response.map {
      case HttpResponse(StatusCodes.OK, headers, entity, _) =>
        val responseBytes: Source[ByteString, Any] = entity.dataBytes

        responseBytes.via(Framing.delimiter(
          ByteString("\n"), maximumFrameLength = 100, allowTruncation = true))
          .map(_.utf8String.parseJson.convertTo[Event])
          .mapMaterializedValue(_ => NotUsed)
    }
  }
}
