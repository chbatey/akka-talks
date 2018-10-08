package info.batey.grpc

import info.batey.akka.StreamingService.{Activity, ActivityRequest}
import info.batey.akka.StreamingService.ActivityServiceGrpc.ActivityService
import io.grpc.stub.StreamObserver

class StreamingServer extends ActivityService {
  override def userActivity(request: ActivityRequest, responseObserver: StreamObserver[Activity]): Unit = {
    responseObserver.onNext(???)
    ???
  }
}
