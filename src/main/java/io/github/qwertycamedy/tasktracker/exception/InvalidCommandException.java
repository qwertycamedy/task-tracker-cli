package io.github.qwertycamedy.tasktracker.exception;

/** Thrown when the user passes an unknown command or wrong arguments. */
public class InvalidCommandException extends TaskTrackerException {

    private static final long serialVersionUID = 1L;

    public InvalidCommandException(String message) {
        super(message);
    }
}
