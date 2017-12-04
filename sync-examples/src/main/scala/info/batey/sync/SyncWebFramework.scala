package info.batey.sync

import info.batey.akka.http.Domain.User

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object WebFramework {
  case class HttpRequest() {
    def getQueryParam(name: String): Int = 1
  }
  case class HttpResponse(statusCode: Int)

  def getUserFromDatabase(id: Int): User = User(id.toString, "chbatey", 21)

  def lookupUser(id: Int): Future[User] =
    Future.successful(User(id.toString, "chbatey", 21))

  object ExternalService {
    def sendPresentToUser(user: User): Unit = {}
  }

  object AsyncExternalService {
    def sendPresentToUser(user: User): Future[Unit] =
      Future.successful(())
  }
}

import WebFramework._

object SyncWebFramework {
  def post(request: HttpRequest): HttpResponse = {
    val userId = request.getQueryParam("userId")
    val user: User = getUserFromDatabase(userId)

    ExternalService.sendPresentToUser(user)

    HttpResponse(200)
  }
}

object AsyncWebFramework {
  def request(request: HttpRequest): Future[HttpResponse] =
    for {
      user <- lookupUser(request.getQueryParam("userId"))
      _ <- AsyncExternalService.sendPresentToUser(user)
    } yield HttpResponse(200)
}
