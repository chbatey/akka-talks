package info.batey.akka
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import info.batey.akka.http.{DataAccess, Domain}

object StreamingService extends App {

  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()

  new StreamingService()


}

class StreamingService(implicit val actorSystem: ActorMaterializer) extends ActivityService {

  override def userActivity(in: ActivityRequest): Source[Activity, NotUsed] = {
    DataAccess.lookupEvents(in.user)
      .map(event => Activity(in.user, event.info))
  }

}
