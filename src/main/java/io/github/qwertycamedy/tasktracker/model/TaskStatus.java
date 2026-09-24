package io.github.qwertycamedy.tasktracker.model;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Lifecycle status of a task. Each constant has the string value
 * used both in the CLI and in the JSON storage file.
 */
public enum TaskStatus {
    TODO("todo"),
    IN_PROGRESS("in-progress"),
    DONE("done");

    private final String value;

    TaskStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    /**
     * Converts a string such as "in-progress" into the matching constant.
     *
     * @throws IllegalArgumentException if the value is not a known status
     */
    public static TaskStatus fromValue(String value) {
        return Arrays.stream(values())
                .filter(status -> status.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown status '" + value + "'. Allowed: " + allowedValues()));
    }

    public static String allowedValues() {
        return Arrays.stream(values())
                .map(TaskStatus::value)
                .collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return value;
    }
}
