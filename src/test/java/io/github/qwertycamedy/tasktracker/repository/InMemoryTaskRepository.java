package io.github.qwertycamedy.tasktracker.repository;

import io.github.qwertycamedy.tasktracker.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/** Test double that keeps tasks in memory instead of a file. */
public class InMemoryTaskRepository implements Repository<Task, Integer> {

    private final Map<Integer, Task> tasks = new TreeMap<>();

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Optional<Task> findById(Integer id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public Task save(Task task) {
        tasks.put(task.id(), task);
        return task;
    }

    @Override
    public boolean deleteById(Integer id) {
        return tasks.remove(id) != null;
    }

    @Override
    public Integer nextId() {
        return tasks.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
    }
}
