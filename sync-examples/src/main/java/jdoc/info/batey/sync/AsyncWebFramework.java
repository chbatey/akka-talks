package jdoc.info.batey.sync;

import info.batey.akka.http.Domain;
import info.batey.sync.WebFramework;

import java.util.concurrent.CompletableFuture;

import static info.batey.sync.WebFramework.*;


public class AsyncWebFramework {
  private Stuff wf = new Stuff();

  //#async-request
  CompletableFuture<HttpResponse> post(HttpRequest request)
  //#async-request
  {

    return null;
  }

  //#async-request2
  CompletableFuture<HttpResponse> request(HttpRequest request) {
    return wf.lookupUser(request.getQueryParam("id"))
        .thenCompose(user -> wf.sendPresentToUser(user))
        .thenApply(v -> new HttpResponse(200));
  }
  //#async-request2

  public static class Stuff {
    public CompletableFuture<Domain.User> lookupUser(String id) {
      return CompletableFuture.completedFuture(new Domain.User(id, "chbatey", 21));
    }

    public CompletableFuture<Void> sendPresentToUser(Domain.User user) {
      return null;
    }
  }
}
