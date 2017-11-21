package info.batey.sync

import info.batey.sync.SyncWebFramework.{HttpRequest, HttpResponse}

object SyncWebFramework {
  type HttpRequest = String
  type HttpResponse = String
}

class SyncWebFramework {
  def doWork(request: HttpRequest): HttpResponse = {
   ???
  }
}
