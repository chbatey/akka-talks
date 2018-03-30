package streams;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Tcp;
import akka.util.ByteString;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class TcpServer {
  public static void main(String[] args) {
    ActorSystem system = ActorSystem.create();
    Materializer mat = ActorMaterializer.create(system);

    Source<Tcp.IncomingConnection, CompletionStage<Tcp.ServerBinding>> bind = Tcp.get(system).bind("127.0.0.1", 8081);
    bind.runForeach(conn -> {
      System.out.println("Got a connection!");
      Sink<ByteString, CompletionStage<Done>> sink = Sink.ignore();
      Source<ByteString, NotUsed> source = Source
              .range(0, 200000000)
              .throttle(5 , new FiniteDuration(100, TimeUnit.MILLISECONDS), 10, ThrottleMode.shaping())
              .map(i -> ByteString.fromString(i.toString() + "\n"));
      conn.handleWith(Flow.fromSinkAndSourceCoupled(sink, source), mat);
    }, mat);
  }
}
