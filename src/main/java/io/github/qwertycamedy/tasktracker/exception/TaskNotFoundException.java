package io.github.qwertycamedy.tasktracker.exception;

/** Thrown when a task with the requested id does not exist. */
public class TaskNotFoundException extends TaskTrackerException {

    private static final long serialVersionUID = 1L;

    public TaskNotFoundException(int id) {
        super("Task with id " + id + " not found");
    }
}
