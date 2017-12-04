package info.batey.sync

import info.batey.sync.SyncWebFramework.{HttpRequest, HttpResponse}

object SyncWebFramework {
  case class HttpRequest() {
    def getQueryParam(name: String): Int = 1
  }
  case class HttpResponse(statusCode: Int)

  case class User(name: String)

  def getUserFromDatabase(id: Int): User = User("chbatey")

  object ExternalService {
    def sendPresentToUser(user: User): Unit = {}
  }
}

import SyncWebFramework._

class SyncWebFramework {
  def post(request: HttpRequest): HttpResponse = {
    val userId = request.getQueryParam("userId")
    val user: User = getUserFromDatabase(userId)

    ExternalService.sendPresentToUser(user)

    HttpResponse(200)
  }
}
