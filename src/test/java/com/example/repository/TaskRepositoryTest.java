package com.example.repository;

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

public class TaskRepositoryTest {
    @TempDir
    Path tempDir;

    private Path dataFile;
    private TaskRepository repository;

    @BeforeEach
    void setUp() {
        dataFile = tempDir.resolve("tasks.json");
        repository = new TaskRepository(dataFile.toFile());
    }

    @Test
    void getAllShouldReturnEmptyListWhenRepositoryIsEmpty() {
        assertTrue(repository.getAll().isEmpty());
    }

    @Test
    void addShouldStoreTaskAndPersistIt() {
        Task task = createTask("Finish project", EPriority.HIGH, EStatus.TODO,
                LocalDate.of(2026, 12, 5));

        repository.add(task);

        assertEquals(1, repository.getAll().size());
        assertSame(task, repository.getById((int) task.getTaskId()));

        TaskRepository loadedRepository = new TaskRepository(dataFile.toFile());
        Task loadedTask = loadedRepository.getById((int) task.getTaskId());

        assertEquals(task.getTitle(), loadedTask.getTitle());
        assertEquals(task.getPriority(), loadedTask.getPriority());
        assertEquals(task.getStatus(), loadedTask.getStatus());
        assertEquals(task.getDeadline(), loadedTask.getDeadline());
    }

    @Test
    void getByIdShouldReturnNullWhenTaskDoesNotExist() {
        assertNull(repository.getById(999));
    }

    @Test
    void updateShouldSaveChangedTaskValues() {
        Task originalTask = createTask("Old title", EPriority.LOW, EStatus.TODO,
                LocalDate.of(2026, 12, 5));
        repository.add(originalTask);

        Task updatedTask = new Task((int) originalTask.getTaskId(), "New title", EPriority.HIGH,
                EStatus.COMPLETED, originalTask.getCreatedAt(), LocalDate.of(2027, 1, 10));

        repository.update(updatedTask);

        TaskRepository loadedRepository = new TaskRepository(dataFile.toFile());
        Task loadedTask = loadedRepository.getById((int) originalTask.getTaskId());

        assertEquals("New title", loadedTask.getTitle());
        assertEquals(EPriority.HIGH, loadedTask.getPriority());
        assertEquals(EStatus.COMPLETED, loadedTask.getStatus());
        assertEquals(LocalDate.of(2027, 1, 10), loadedTask.getDeadline());
    }

    @Test
    void removeByIdShouldRemoveExistingTaskAndPersistChange() {
        Task task = createTask("Remove me", EPriority.MEDIUM, EStatus.TODO,
                LocalDate.of(2026, 12, 5));
        repository.add(task);

        boolean removed = repository.removeById((int) task.getTaskId());

        assertTrue(removed);
        assertTrue(repository.getAll().isEmpty());
        assertTrue(new TaskRepository(dataFile.toFile()).getAll().isEmpty());
    }

    @Test
    void removeByIdShouldReturnFalseWhenTaskDoesNotExist() {
        assertFalse(repository.removeById(999));
    }

    @Test
    void getByPriorityShouldReturnOnlyTasksWithRequestedPriority() {
        Task lowTask = createTask("Low task", EPriority.LOW, EStatus.TODO,
                LocalDate.of(2026, 12, 5));
        Task highTask = createTask("High task", EPriority.HIGH, EStatus.TODO,
                LocalDate.of(2026, 12, 6));
        repository.add(lowTask);
        repository.add(highTask);

        List<Task> result = repository.getByPriority(EPriority.HIGH);

        assertEquals(1, result.size());
        assertSame(highTask, result.get(0));
    }

    @Test
    void getByStatusShouldReturnOnlyTasksWithRequestedStatus() {
        Task todoTask = createTask("Todo task", EPriority.MEDIUM, EStatus.TODO,
                LocalDate.of(2026, 12, 5));
        Task completedTask = createTask("Completed task", EPriority.MEDIUM, EStatus.COMPLETED,
                LocalDate.of(2026, 12, 6));
        repository.add(todoTask);
        repository.add(completedTask);

        List<Task> result = repository.getByStatus(EStatus.COMPLETED);

        assertEquals(1, result.size());
        assertSame(completedTask, result.get(0));
    }

    @Test
    void getByDeadlineShouldReturnTasksDueOnOrBeforeDate() {
        Task overdueTask = createTask("Overdue task", EPriority.LOW, EStatus.TODO,
                LocalDate.of(2026, 10, 20));
        Task dueTodayTask = createTask("Due today", EPriority.MEDIUM, EStatus.TODO,
                LocalDate.of(2026, 10, 28));
        Task futureTask = createTask("Future task", EPriority.HIGH, EStatus.TODO,
                LocalDate.of(2026, 11, 10));
        Task taskWithoutDeadline = createTask("No deadline", EPriority.LOW, EStatus.TODO,
                LocalDate.of(2026, 11, 1));
        taskWithoutDeadline.setDeadline(null);

        repository.add(overdueTask);
        repository.add(dueTodayTask);
        repository.add(futureTask);
        repository.add(taskWithoutDeadline);

        List<Task> result = repository.getByDeadline(LocalDate.of(2026, 10, 28));

        assertEquals(2, result.size());
        assertTrue(result.contains(overdueTask));
        assertTrue(result.contains(dueTodayTask));
        assertFalse(result.contains(futureTask));
        assertFalse(result.contains(taskWithoutDeadline));
    }

    private Task createTask(String title, EPriority priority, EStatus status, LocalDate deadline) {
        return new Task(title, priority, status, deadline);
    }
}
