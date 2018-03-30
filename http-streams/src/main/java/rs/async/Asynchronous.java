package rs.async;

import java.util.List;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

import static akka.actor.ActorRef.noSender;

public class Asynchronous {
  static class Task {}
  static class Result {}

  //#actor
  static class MyActor extends AbstractActor {
    @Override
    public Receive createReceive() {
      return receiveBuilder()
              .match(Task.class, task -> { /* do work */ })
              .build();
    }
  }
  //#actor

  public void performTasks() {
    ActorRef actor = null;
    List<Task> tasks = null;

    //#enqueue
    for (Task task: tasks) {
      actor.tell(task, noSender());
    }
    //#enqueue
  }
}
