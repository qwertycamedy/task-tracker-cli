package io.github.qwertycamedy.tasktracker.exception;

/**
 * Base class for all expected application errors. The CLI catches it
 * and prints a friendly message instead of a stack trace.
 */
public class TaskTrackerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TaskTrackerException(String message) {
        super(message);
    }

    public TaskTrackerException(String message, Throwable cause) {
        super(message, cause);
    }
}
