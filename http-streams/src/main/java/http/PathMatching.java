package http;

import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.PathMatcher1;
import akka.http.javadsl.server.PathMatchers;
import akka.http.javadsl.server.Route;

import static akka.http.javadsl.server.PathMatchers.*;

public class PathMatching extends AllDirectives {
  void seg() {
    //#segment
    Route route = path(PathMatchers.segment("hello"), () ->
      complete("Hello, World!"));
    //#segment
  }
  void staticimports() {
    //#staticimports
    Route route = path(segment("hello"), () ->
      complete("Hello, World!"));
    //#staticimports
  }
  void segments() {
    //#segments
    Route route = path(segment("hello").slash(segment()), name ->
      complete("Hello, " + name + "!"));
    //#segments
  }
  void advanced() {
    //#advanced
    PathMatcher1<Integer> m =
      PathMatchers
        .segment("foo")
        .slash("bar")
        .slash(
          segment("X").concat(integerSegment())
        )
        .slash(
          segment("edit").orElse(segment("create"))
        );
    //#advanced
  }
}
