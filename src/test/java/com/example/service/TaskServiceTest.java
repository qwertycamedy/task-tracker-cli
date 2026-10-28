package com.example.service;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.example.model.EPriority;
import com.example.model.EStatus;
import com.example.model.Task;
import com.example.repository.TaskRepository;

public class TaskServiceTest {
    @TempDir
    Path tempDir;

    private TaskService service;

    @BeforeEach
    void setUp() {
        TaskRepository repository = new TaskRepository(tempDir.resolve("tasks.json").toFile());
        service = new TaskService(repository);
    }

    @Test
    void addShouldStoreTaskAndMakeItAvailableById() {
        Task task = createTask("Finish project", EPriority.HIGH, EStatus.TODO,
                LocalDate.of(2026, 12, 5));

        service.add(task);

        assertSame(task, service.getById((int) task.getTaskId()));
    }

    @Test
    void getByIdShouldReturnNullWhenTaskDoesNotExist() {
        assertNull(service.getById(999));
    }

    @Test
    void updateShouldChangeTaskValues() {
        Task originalTask = createTask("Old title", EPriority.LOW, EStatus.TODO,
                LocalDate.of(2026, 12, 5));
        service.add(originalTask);

        Task updatedTask = new Task((int) originalTask.getTaskId(), "New title", EPriority.HIGH,
                EStatus.COMPLETED, originalTask.getCreatedAt(), LocalDate.of(2027, 1, 10));

        service.update(updatedTask);

        Task result = service.getById((int) originalTask.getTaskId());
        assertEquals("New title", result.getTitle());
        assertEquals(EPriority.HIGH, result.getPriority());
        assertEquals(EStatus.COMPLETED, result.getStatus());
        assertEquals(LocalDate.of(2027, 1, 10), result.getDeadline());
    }

    @Test
    void removeByIdShouldReturnTrueAndRemoveTask() {
        Task task = createTask("Remove me", EPriority.MEDIUM, EStatus.TODO,
                LocalDate.of(2026, 12, 5));
        service.add(task);

        assertTrue(service.removeById((int) task.getTaskId()));
        assertNull(service.getById((int) task.getTaskId()));
    }

    @Test
    void removeByIdShouldReturnFalseWhenTaskDoesNotExist() {
        assertFalse(service.removeById(999));
    }

    @Test
    void getAllShouldReturnAllTasks() {
        Task firstTask = createTask("First task", EPriority.LOW, EStatus.TODO,
                LocalDate.of(2026, 12, 5));
        Task secondTask = createTask("Second task", EPriority.HIGH, EStatus.COMPLETED,
                LocalDate.of(2026, 12, 6));
        service.add(firstTask);
        service.add(secondTask);

        List<Task> result = service.getAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(firstTask));
        assertTrue(result.contains(secondTask));
    }

    @Test
    void getByStatusShouldReturnOnlyMatchingTasks() {
        Task todoTask = createTask("Todo task", EPriority.MEDIUM, EStatus.TODO,
                LocalDate.of(2026, 12, 5));
        Task completedTask = createTask("Completed task", EPriority.MEDIUM, EStatus.COMPLETED,
                LocalDate.of(2026, 12, 6));
        service.add(todoTask);
        service.add(completedTask);

        List<Task> result = service.getByStatus(EStatus.COMPLETED);

        assertEquals(1, result.size());
        assertSame(completedTask, result.get(0));
    }

    @Test
    void getByPriorityShouldReturnOnlyMatchingTasks() {
        Task lowTask = createTask("Low task", EPriority.LOW, EStatus.TODO,
                LocalDate.of(2026, 12, 5));
        Task highTask = createTask("High task", EPriority.HIGH, EStatus.TODO,
                LocalDate.of(2026, 12, 6));
        service.add(lowTask);
        service.add(highTask);

        List<Task> result = service.getByPriority(EPriority.HIGH);

        assertEquals(1, result.size());
        assertSame(highTask, result.get(0));
    }

    @Test
    void getByDeadlineShouldReturnTasksDueOnOrBeforeDate() {
        Task overdueTask = createTask("Overdue task", EPriority.LOW, EStatus.TODO,
                LocalDate.of(2026, 10, 20));
        Task dueTodayTask = createTask("Due today", EPriority.MEDIUM, EStatus.TODO,
                LocalDate.of(2026, 10, 28));
        Task futureTask = createTask("Future task", EPriority.HIGH, EStatus.TODO,
                LocalDate.of(2026, 11, 10));
        service.add(overdueTask);
        service.add(dueTodayTask);
        service.add(futureTask);

        List<Task> result = service.getByDeadline(LocalDate.of(2026, 10, 28));

        assertEquals(2, result.size());
        assertTrue(result.contains(overdueTask));
        assertTrue(result.contains(dueTodayTask));
        assertFalse(result.contains(futureTask));
    }

    private Task createTask(String title, EPriority priority, EStatus status, LocalDate deadline) {
        return new Task(title, priority, status, deadline);
    }
}
