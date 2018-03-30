package sc.http

import akka.http.scaladsl.server.util.Tuple
import akka.http.scaladsl.server.{Directive, PathMatcher}

abstract class PathApi {
  //#pathApi1
  def path[L](pm: PathMatcher[L]): Directive[L]
  //#pathApi1
  //#pathApi
  abstract class PathMatcher[L: Tuple]
  type PathMatcher0 = PathMatcher[Unit]
  type PathMatcher1[T] = PathMatcher[Tuple1[T]]
  type PathMatcher2[T,U] = PathMatcher[Tuple2[T,U]]
  // .. etc
  //#pathApi
}
