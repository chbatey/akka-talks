package info.batey.akka.http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import info.batey.akka.http.Domain.{Event, User}
import spray.json._

object JsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val userFormat = jsonFormat3(User)
  implicit val eventFormat = jsonFormat3(Event)
}
