package io.github.qwertycamedy.tasktracker.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable task entity. Instead of setters, "with" methods return
 * an updated copy, which keeps the object safe to share.
 */
public record Task(
        int id,
        String description,
        TaskStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public Task {
        if (id <= 0) {
            throw new IllegalArgumentException("Task id must be positive");
        }
        Objects.requireNonNull(description, "description");
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(createdAt, "createdAt");
        Objects.requireNonNull(updatedAt, "updatedAt");
        if (description.isBlank()) {
            throw new IllegalArgumentException("Task description must not be blank");
        }
    }

    /** Creates a brand-new task with status {@link TaskStatus#TODO}. */
    public static Task create(int id, String description, LocalDateTime now) {
        return new Task(id, description.strip(), TaskStatus.TODO, now, now);
    }

    public Task withDescription(String newDescription, LocalDateTime now) {
        return new Task(id, newDescription.strip(), status, createdAt, now);
    }

    public Task withStatus(TaskStatus newStatus, LocalDateTime now) {
        return new Task(id, description, newStatus, createdAt, now);
    }
}
