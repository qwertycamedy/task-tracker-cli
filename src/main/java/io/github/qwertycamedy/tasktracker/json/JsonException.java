package io.github.qwertycamedy.tasktracker.json;

/** Thrown when JSON text is malformed. */
public class JsonException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JsonException(String message) {
        super(message);
    }
}
