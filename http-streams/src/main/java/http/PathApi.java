package http;

import akka.http.javadsl.server.PathMatcher0;
import akka.http.javadsl.server.PathMatcher1;
import akka.http.javadsl.server.PathMatcher2;
import akka.http.javadsl.server.Route;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Show what the path directives API looks like in Java interface methods
 */
public class PathApi {
  //#pathApi1
  Route path(String segment, Supplier<Route> inner) {
    //#pathApi1
    return null;
  }

  //#pathApi
           Route path(PathMatcher0 p, Supplier<Route> inner) {
    //#pathApi
    return null;
  }

  //#pathApi
       <T> Route path(PathMatcher1<T> p, Function<T, Route> inner) {
    //#pathApi
    return null;
  }

  //#pathApi
  <T1, T2> Route path(PathMatcher2<T1, T2> p, BiFunction<T1, T2, Route> inner) {
    //#pathApi
    return null;
  }

}
