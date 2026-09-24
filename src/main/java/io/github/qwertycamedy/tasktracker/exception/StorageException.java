package io.github.qwertycamedy.tasktracker.exception;

/** Thrown when the tasks file cannot be read, written or parsed. */
public class StorageException extends TaskTrackerException {

    private static final long serialVersionUID = 1L;

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
