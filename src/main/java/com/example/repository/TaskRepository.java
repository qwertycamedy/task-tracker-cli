package com.example.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.model.EPriority;
import com.example.model.EStatus;
import com.example.model.Task;

public class TaskRepository implements ITaskRepository<Task> {
    private static final String FILE_PATH = "tasks.json";

    private ArrayList<Task> data;
    private final File file;
    private final ObjectMapper mapper;

    public TaskRepository() {
        this(new File(FILE_PATH));
    }

    public TaskRepository(File file) {
        this.data = new ArrayList<>();
        this.file = file;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());

        verifyFile();
        loadTasks();
    }

    @Override
    public void add(Task task) {
        data.add(task);
        saveTasks();
    }

    @Override
    public Task getById(int taskId) {
        return this.data.stream().filter(t -> t.getTaskId() == taskId).findFirst().orElse(null);
    }

    @Override
    public void update(Task task) {
        this.data.stream().filter(t -> t.getTaskId() == task.getTaskId()).forEach(t -> {
            t.setTitle(task.getTitle());
            t.setPriority(task.getPriority());
            t.setStatus(task.getStatus());
            t.setDeadline(task.getDeadline());
        });
        saveTasks();
    }

    @Override
    public boolean removeById(int taskId) {
        boolean status = this.data.removeIf(t -> t.getTaskId() == taskId);

        if (status) {
            saveTasks();
        }

        return status;
    }

    @Override
    public List<Task> getAll() {
        return this.data;
    }

    @Override
    public List<Task> getByPriority(EPriority priority) {
        return this.data.stream().filter(t -> t.getPriority().equals(priority)).toList();
    }

    @Override
    public List<Task> getByStatus(EStatus status) {
        return this.data.stream().filter(t -> t.getStatus().equals(status)).toList();
    }

    @Override
    public List<Task> getByDeadline(LocalDate deadline) {
        return this.data.stream().filter(t -> t.getDeadline() != null && !t.getDeadline().isAfter(deadline)).toList();
    }

    private void verifyFile() {
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadTasks() {
        if (this.file.length() == 0) {
            return;
        }

        try {
            this.data = mapper.readValue(this.file, new TypeReference<ArrayList<Task>>() {
            });
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e);
        }
    }

    private void saveTasks() {
        try {
            mapper.writeValue(this.file, this.data);
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e);
        }
    }
}
