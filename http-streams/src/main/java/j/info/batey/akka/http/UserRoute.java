package j.info.batey.akka.http;

import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Route;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import info.batey.akka.http.Domain;
import info.batey.akka.http.Domain.User;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.PathMatchers.*;
import static scala.compat.java8.JFunction.func;


public class UserRoute extends AllDirectives {
  public static void main(String[] args) throws Exception {
    ActorSystem system = ActorSystem.create("routes");

    final Http http = Http.get(system);
    final ActorMaterializer materializer = ActorMaterializer.create(system);

    //In order to access all directives we need an instance where the routes are define.
    UserRoute app = new UserRoute();

    final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow =
      app.userRoute()
        .flow(system, materializer);

    final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
      ConnectHttp.toHost("localhost", 8080), materializer);

    System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
    System.in.read(); // let it run until user presses return

    binding
      .thenCompose(ServerBinding::unbind) // trigger unbinding from the port
      .thenAccept(unbound -> system.terminate()); // and shutdown when done
  }

  private DataAccess dataAccess;

  private Route userRoute() {
    return
      route(path(segment("user").slash(segment()), id ->
        //#user-route
        get(() -> {
            CompletableFuture<Optional<User>> user =
              dataAccess.lookupUser(id);
            return onSuccess(user, (Optional<User> opUser) ->
              opUser.map(u -> complete(u.serialise()))
                    .orElse(complete(StatusCodes.NOT_FOUND)));
          }
        //#user-route
        )));
  }
}
