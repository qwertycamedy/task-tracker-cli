package com.example.service;

import java.time.LocalDate;
import java.util.List;

import com.example.model.EPriority;
import com.example.model.EStatus;
import com.example.model.Task;
import com.example.repository.TaskRepository;

public class TaskService implements ITaskService<Task> {
    private final TaskRepository repository;

    public TaskService() {
        this.repository = new TaskRepository();
    }

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public void add(Task task) {
        this.repository.add(task);
    }

    @Override
    public Task getById(int taskId) {
        return this.repository.getById(taskId);
    }

    @Override
    public void update(Task task) {
        this.repository.update(task);
    }

    @Override
    public boolean removeById(int taskid) {
        return this.repository.removeById(taskid);
    }

    @Override
    public List<Task> getAll() {
        return this.repository.getAll();
    }

    @Override
    public List<Task> getByStatus(EStatus status) {
        return this.repository.getByStatus(status);
    }

    @Override
    public List<Task> getByPriority(EPriority priority) {
        return this.repository.getByPriority(priority);
    }

    @Override
    public List<Task> getByDeadline(LocalDate deadline) {
        return this.repository.getByDeadline(deadline);
    }
}
