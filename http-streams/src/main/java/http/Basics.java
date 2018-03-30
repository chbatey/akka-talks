package http;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.IncomingConnection;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import static java.lang.System.out;

import java.util.concurrent.CompletionStage;

public class Basics {
  public static void main(String[] args) {
    new Basics().run();
  }
  public void run() {
    //#init
    ActorSystem system = ActorSystem.create();
    Http http = Http.get(system);
    //#init

    //#bind
    Source<IncomingConnection, CompletionStage<ServerBinding>> bind =
      http.bind(ConnectHttp.toHost("localhost", 0));
    //#bind

    //#run
    Materializer materializer = ActorMaterializer.create(system);

    CompletionStage<ServerBinding> bound = bind
      .to(Sink.foreach(c -> out.println("Got HTTP connection!")))
      .run(materializer);
    //#run

    //#log
    bound.thenAccept(b ->
      out.println("Bound on " + b.localAddress()));
    //#log
  }

  void flow() {
    IncomingConnection connection = null;
    //#flow
    Flow<HttpResponse, HttpRequest, NotUsed> flow =
      connection.flow();

    // Materialize and run it yourself
    //#flow
  }
  void handleWith() {
    IncomingConnection connection = null;
    Materializer materializer = null;
    Flow<HttpRequest, HttpResponse, NotUsed> myflow = null;
    //#handleWith
    // Construct your flow:
    Flow<HttpRequest, HttpResponse, NotUsed> flow = myflow;

    // Use it to handle the connection:
    connection.handleWith(flow, materializer);
    //#handleWith
  }
  void bindAndHandle() {
    Materializer materializer = null;
    Flow<HttpRequest, HttpResponse, NotUsed> myflow = null;
    Http http = null;
    //#bindAndHandle
    // Construct your flow:
    Flow<HttpRequest, HttpResponse, NotUsed> flow = myflow;

    // Use it to handle connections:
    http.bindAndHandle(
      flow,
      ConnectHttp.toHost("localhost", 8080),
      materializer);
    //#bindAndHandle
  }
}
