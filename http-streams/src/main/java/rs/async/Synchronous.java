package rs.async;

import java.util.List;

public class Synchronous {
  static class Task {}
  static class Result {}

  static
  //#service
  class MyService {
    public Result performTask(Task task) throws Exception {
      // ... do work
      //#service
      return null;
      //#service
    }
  }
  //#service

  public void performTasks() throws Exception {
    MyService service = new MyService();
    List<Task> tasks = null;

    //#perform
    for (Task task: tasks) {
      Result result = service.performTask(task);
      // Handle result...
    }
    //#perform
  }
}
