package io.github.qwertycamedy.tasktracker.service;

import io.github.qwertycamedy.tasktracker.exception.TaskNotFoundException;
import io.github.qwertycamedy.tasktracker.model.Task;
import io.github.qwertycamedy.tasktracker.model.TaskStatus;
import io.github.qwertycamedy.tasktracker.repository.Repository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Business logic of the task tracker. It depends only on the
 * {@link Repository} abstraction, so storage can be swapped (for example,
 * an in-memory repository in unit tests). The {@link Clock} is injected
 * so timestamps are predictable in tests.
 */
public class TaskService {

    private final Repository<Task, Integer> repository;
    private final Clock clock;

    public TaskService(Repository<Task, Integer> repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository);
        this.clock = Objects.requireNonNull(clock);
    }

    public Task add(String description) {
        Task task = Task.create(repository.nextId(), description, now());
        return repository.save(task);
    }

    public Task updateDescription(int id, String description) {
        return modify(id, task -> task.withDescription(description, now()));
    }

    public Task changeStatus(int id, TaskStatus status) {
        return modify(id, task -> task.withStatus(status, now()));
    }

    public void delete(int id) {
        if (!repository.deleteById(id)) {
            throw new TaskNotFoundException(id);
        }
    }

    /** @param status filter, or empty to return all tasks */
    public List<Task> list(Optional<TaskStatus> status) {
        return repository.findAll().stream()
                .filter(task -> status.map(s -> task.status() == s).orElse(true))
                .sorted(Comparator.comparingInt(Task::id))
                .toList();
    }

    /** Counts tasks per status, keeping enum order (todo, in-progress, done). */
    public Map<TaskStatus, Long> statistics() {
        Map<TaskStatus, Long> stats = repository.findAll().stream()
                .collect(Collectors.groupingBy(Task::status,
                        () -> new EnumMap<>(TaskStatus.class),
                        Collectors.counting()));
        for (TaskStatus status : TaskStatus.values()) {
            stats.putIfAbsent(status, 0L);
        }
        return stats;
    }

    private Task modify(int id, Function<Task, Task> change) {
        Task existing = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return repository.save(change.apply(existing));
    }

    private LocalDateTime now() {
        return LocalDateTime.now(clock).withNano(0);
    }
}
