package streams;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class Materialization {
  void run() {
    //#multiple
    RunnableGraph<NotUsed> graph = Source.range(0, 20000000)
      .map(Object::toString)
      .to(Sink.foreach(str -> System.out.println(str)));

    ActorSystem system = ActorSystem.create();
    Materializer materializer = ActorMaterializer.create(system);

    NotUsed n1 = graph.run(materializer);
    NotUsed n2 = graph.run(materializer);
    //#multiple

    //#source
    Source<String, ActorRef> source =
      Source.actorRef(23, OverflowStrategy.dropNew());

    Sink<String, CompletionStage<String>> sink =
      Sink.reduce((l, r) -> l + r);

    ActorRef actor = source.to(sink).run(materializer);
    actor.tell("Message", ActorRef.noSender());
    //#source
  }

  void sink() {
    //#sink
    Source<String, ActorRef> source =
      Source.actorRef(23, OverflowStrategy.dropNew());

    Sink<String, CompletionStage<String>> sink =
      Sink.reduce((l, r) -> l + r);

    RunnableGraph<ActorRef> graph1 =
      source.to(sink);

    RunnableGraph<CompletionStage<String>> graph2 =
      source.toMat(sink, Keep.right());

    RunnableGraph<Pair<ActorRef, CompletionStage<String>>> graph3 =
      source.toMat(sink, Keep.both());
    //#sink

    //#fusing
    Source.range(1, 3)
      .map(x -> x + 1)
      .map(x -> x * 2)
      .to(Sink.reduce((x, y) -> x + y));
    //#fusing

    //#fusing-explicit-async
    Source.range(1, 3)
      .map(x -> x + 1).async()
      .map(x -> x * 2)
      .to(Sink.reduce((x, y) -> x + y));
    //#fusing-explicit-async

    //#fusing-async
    Source.range(1, 3)
      .map(x -> x + 1)
      .mapAsync(5, n -> CompletableFuture.completedFuture(n * 2))
      .to(Sink.reduce((x, y) -> x + y));
    //#fusing-async
  }
}
