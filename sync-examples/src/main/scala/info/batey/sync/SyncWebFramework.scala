package info.batey.sync

import info.batey.akka.http.Domain.User

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object ExternalService {
  def sendPresentToUser(user: User): Unit = {}
}

object WebFramework {
  case class HttpRequest() {
    def getQueryParam(name: String): String = "param"
  }
  case class HttpResponse(statusCode: Int)

  def getUserFromDatabase(id: String): User = User(id.toString, "chbatey", 21)

  def lookupUser(id: String): Future[User] =
    Future.successful(User(id.toString, "chbatey", 21))


  object AsyncExternalService {
    def sendPresentToUser(user: User): Future[Unit] =
      Future.successful(())
  }
}

import WebFramework._

object SyncWebFramework {
  {
    //#sync-post
    def post(request: HttpRequest): HttpResponse = {
      val userId = request.getQueryParam("userId")
      val user: User = getUserFromDatabase(userId)

      ExternalService.sendPresentToUser(user)

      HttpResponse(200)
    }
    //#sync-post
  }
  {
    //#sync-post-times
    def post(request: HttpRequest): HttpResponse = {
      // nice and quick
      val userId = request.getQueryParam("userId")
      // 5 millis to 10 seconds?
      val user: User = getUserFromDatabase(userId)

      // 5 millis to 10 seconds?
      ExternalService.sendPresentToUser(user)

      HttpResponse(200)
    }
    //#sync-post-times
  }
}

object AsyncWebFramework {
  //#async-request
  def request(request: HttpRequest): Future[HttpResponse] =
    for {
      user <- lookupUser(request.getQueryParam("userId"))
      _ <- AsyncExternalService.sendPresentToUser(user)
    } yield HttpResponse(200)
  //#async-request
}
