package info.batey.akka
import akka.NotUsed
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.Future

// TODO extend this example to do something with Alpakka
class GreeterServiceImpl(materializer: ActorMaterializer) extends GreeterService {

  import materializer.executionContext
  private implicit val mat: ActorMaterializer = materializer


  override def sayHello(in: HelloRequest): Future[HelloReply] = {
    Future.successful(HelloReply(s"Hello ${in.name}"))
  }

  override def itKeepsTalking(in: Source[HelloRequest, NotUsed]): Future[HelloReply] = {
    in.runWith(Sink.foreach(println))
      .map(_ => HelloReply("I have read your stream"))

  }

  override def itKeepsReplying(in: HelloRequest): Source[HelloReply, NotUsed] = {
    Source(List(HelloReply(s"Hello"), HelloReply(s"Hello ${in.name}")))
  }

  override def streamHellos(in: Source[HelloRequest, NotUsed]): Source[HelloReply, NotUsed] = {
    in.map(hr => HelloReply(s"Hello ${hr.name}"))
  }

}
