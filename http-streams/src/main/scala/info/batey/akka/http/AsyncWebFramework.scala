package info.batey.akka.http

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.Future

object AsyncWebFramework {
  //#async-request
  def request(request: HttpRequest): Future[HttpResponse] = ???
  //#async-request

}
