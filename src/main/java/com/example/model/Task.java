package com.example.model;

import java.time.LocalDate;
import java.util.Random;

import com.example.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Task {
    Random random = new Random();

    public static int id = 0;
    private int taskId;

    private String title;

    private EPriority priority;
    private EStatus status;

    private final LocalDate createdAt;
    private LocalDate deadline;

    @JsonCreator
    public Task(@JsonProperty("taskId") int taskId,
            @JsonProperty("title") String title,
            @JsonProperty("priority") EPriority priority,
            @JsonProperty("status") EStatus status,
            @JsonProperty("createdAt") LocalDate createdAt,
            @JsonProperty("deadline") LocalDate deadline) {
        id = taskId;
        this.taskId = taskId;

        this.title = title;

        this.priority = priority;
        this.status = status;

        this.createdAt = createdAt;
        this.deadline = deadline;
    }

    public Task(
            String title, EPriority priority, EStatus status, LocalDate deadline) {
        this.taskId = ++id;

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Task title cannot be blank!");
        }
        this.title = title.trim();

        this.priority = priority == null ? EPriority.LOW : priority;
        this.status = status == null ? EStatus.TODO : status;

        this.createdAt = LocalDate.now();
        this.deadline = deadline == null ? LocalDate.now().plusDays(10) : deadline;
    }

    public long getTaskId() {
        return this.taskId;
    }

    public LocalDate getCreatedAt() {
        return this.createdAt;
    }

    public String getTitle() {
        return this.title;
    }

    public EPriority getPriority() {
        return this.priority;
    }

    public EStatus getStatus() {
        return this.status;
    }

    public LocalDate getDeadline() {
        return this.deadline;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPriority(EPriority priority) {
        this.priority = priority;
    }

    public void setStatus(EStatus status) {
        this.status = status;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "#" + this.taskId + " [" + this.priority.getLabel() + "] - " + this.title + " - "
                + this.status.getLabel() + " - deadline: "
                + DateUtils.formatDate(this.deadline);
    }
}
