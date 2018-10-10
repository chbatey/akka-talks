package info.batey.grpc

import akka.NotUsed
import akka.stream.scaladsl.Source
import info.batey.akka.StreamingService.ActivityServiceGrpc.ActivityService
import info.batey.akka.StreamingService.{Activity, ActivityRequest}
import info.batey.akka.http.Domain.{Event, User}
import io.grpc.stub.StreamObserver

import scala.concurrent.Future

trait EventStream {
 def customerEventStream(): Source[Event, NotUsed]
}

trait DataAccess {
  def lookupUser(name: String): Future[User]
}

class StreamingServer(eventStream: EventStream, dataAccess: DataAccess) extends ActivityService {

  override def userActivity(request: ActivityRequest, responseObserver: StreamObserver[Activity]): Unit = {
    // Write to Cassandra
    // Nice and easy

    // Then kick off a query from Kafka or Cassandra that
    // return s many results. How do you decide how fast to
    // call onNext???
    responseObserver.onNext(???)
    ???
  }

}
