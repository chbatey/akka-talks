package jdoc.info.batey.sync;

import info.batey.akka.http.Domain;
import info.batey.sync.ExternalService;

import static info.batey.sync.WebFramework.*;

public class SyncWebFramework {
  //#sync-post
  HttpResponse post(HttpRequest request) {
    String userId = request.getQueryParam("userId");
    Domain.User user = getUserFromDatabase(userId);
    ExternalService.sendPresentToUser(user);
    return new HttpResponse(200);
  }
  //#sync-post

  public class SyncWebFrameworkTimes {
    //#sync-post-times
    private HttpResponse post(HttpRequest request) {
      // nice and quick
      String userId = request.getQueryParam("userId");
      // 5 millis to 10 seconds?
      Domain.User user = getUserFromDatabase(userId);

      // 5 millis to 10 seconds?
      ExternalService.sendPresentToUser(user);

      return new HttpResponse(200);
    }
    //#sync-post-times

  }
}
