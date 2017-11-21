package info.batey.akka

import java.util.concurrent.Executor

import com.google.common.util.concurrent.ListenableFuture

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Try

package object http{

   implicit def listenableFutureToFuture[A](lf: ListenableFuture[A])(implicit executionContext: ExecutionContext): Future[A] = {
    val promise = Promise[A]
    lf.addListener(() => promise.complete(Try(lf.get())), executionContext.asInstanceOf[Executor])
    promise.future
  }
}
