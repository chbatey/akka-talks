package j.info.batey.akka.http;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import info.batey.akka.http.Domain;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static info.batey.akka.http.Domain.*;

public class DataAccess {

  public static final String CQL = "select * from users where user_id = ?";
  private final Session session;

  public DataAccess(Session session) {
    this.session = session;
  }

  //#db-call
  CompletableFuture<Optional<User>> lookupUser(String userId) {
    CompletableFuture<ResultSet> rs = toCompletableFuture(
      session.executeAsync(CQL, userId));

    CompletableFuture<Optional<User>> user =
      rs.thenApply((ResultSet rSet) ->
        Optional.ofNullable(rSet.one())
          .map((Row row) -> new User(
            row.getString("user_id"),
            row.getString("user_name"),
            row.getInt("age")))
      );

    return user;
  }

  //#db-call
  static <T> CompletableFuture<T> toCompletableFuture(ListenableFuture<T> listenableFuture) {
    final CompletableFuture<T> completableFuture = new CompletableFuture<T>();
    Futures.addCallback(listenableFuture, new FutureCallback<T>() {
      public void onFailure(Throwable t) {
        completableFuture.completeExceptionally(t);
      }

      public void onSuccess(T t) {
        completableFuture.complete(t);
      }
    });
    return completableFuture;
  }
}
