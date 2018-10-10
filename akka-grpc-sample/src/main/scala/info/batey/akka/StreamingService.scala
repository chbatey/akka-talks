package info.batey.akka

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import info.batey.akka.http.{DataAccess, Domain}

import scala.concurrent.{ExecutionContext, Future}

object StreamingService extends App {

  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = actorSystem.dispatcher

  val service = new StreamingService()


}

class StreamingService(implicit
                       actorSystem: ActorMaterializer,
                       ec: ExecutionContext) extends ActivityService {

  override def userActivity(in: ActivityRequest): Source[Activity, NotUsed] = {

    val userInfo: Future[Option[Domain.User]] = DataAccess.lookupUser(in.user)
    val streamOfUserActivity: Future[Source[Activity, NotUsed]] = userInfo.map {
      case Some(user) =>
        DataAccess.lookupEvents(user.name)
          .map(event => Activity(user.name, event.info))
      case None =>
        Source.empty
    }
    Source.fromFutureSource(streamOfUserActivity).mapMaterializedValue(_ => NotUsed)
  }

}
