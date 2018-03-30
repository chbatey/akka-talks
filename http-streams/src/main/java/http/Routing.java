package http;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.*;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class Routing extends AllDirectives {

  void run() {
    ActorSystem system = ActorSystem.create("routes");

    Http http = Http.get(system);
    Materializer materializer = ActorMaterializer.create(system);

    //#simple
    Route route = path("hello", () ->
      get(() ->
        complete("Hello, world!")
      )
    );
    //#simple
    //#run
    Flow<HttpRequest, HttpResponse, NotUsed> flow =
      route.flow(system, materializer);
    //#run
  }
}
