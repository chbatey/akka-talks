package streams;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.util.concurrent.CompletionStage;

public class Intro {
  void run() {
    //#source
    Source<Integer, NotUsed> source =
      //#source-no
      Source.range(0, 20000000);
      //#source-no
    //#source
    //#flow
    Flow<Integer, String, NotUsed> flow =
      //#flow-no
      Flow.fromFunction(n -> n.toString());
      //#flow-no
    //#flow
    //#sink
    Sink<String, CompletionStage<Done>> sink =
      //#sink-no
      Sink.foreach(str -> System.out.println(str));
      //#sink-no
    //#sink
    //#graph
    RunnableGraph<NotUsed> runnable =
      source.via(flow).to(sink);
    //#graph
    //#run
    ActorSystem system = ActorSystem.create();
    Materializer materializer = ActorMaterializer.create(system);

    runnable.run(materializer);
    //#run

    //#short
    Source.range(0, 20000000)
      .map(Object::toString)
      .runForeach(str -> System.out.println(str), materializer);
    //#short
  }
}
