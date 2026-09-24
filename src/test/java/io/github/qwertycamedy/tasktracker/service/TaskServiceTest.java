package io.github.qwertycamedy.tasktracker.service;

import io.github.qwertycamedy.tasktracker.exception.TaskNotFoundException;
import io.github.qwertycamedy.tasktracker.model.Task;
import io.github.qwertycamedy.tasktracker.model.TaskStatus;
import io.github.qwertycamedy.tasktracker.repository.InMemoryTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskServiceTest {

    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");

    private TaskService service;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(NOW, ZoneOffset.UTC);
        service = new TaskService(new InMemoryTaskRepository(), fixedClock);
    }

    @Test
    @DisplayName("add() assigns sequential ids and TODO status")
    void addAssignsIdsAndDefaultStatus() {
        Task first = service.add("First");
        Task second = service.add("  Second  ");

        assertEquals(1, first.id());
        assertEquals(2, second.id());
        assertEquals("Second", second.description());
        assertEquals(TaskStatus.TODO, first.status());
        assertEquals(LocalDateTime.of(2026, 1, 15, 10, 0), first.createdAt());
    }

    @Test
    void updateDescriptionChangesOnlyDescription() {
        Task task = service.add("Old");

        Task updated = service.updateDescription(task.id(), "New");

        assertEquals("New", updated.description());
        assertEquals(task.status(), updated.status());
        assertEquals(task.createdAt(), updated.createdAt());
    }

    @Test
    void changeStatusUpdatesStatus() {
        Task task = service.add("Task");

        assertEquals(TaskStatus.IN_PROGRESS, service.changeStatus(task.id(), TaskStatus.IN_PROGRESS).status());
        assertEquals(TaskStatus.DONE, service.changeStatus(task.id(), TaskStatus.DONE).status());
    }

    @Test
    void operationsOnMissingTaskThrow() {
        assertThrows(TaskNotFoundException.class, () -> service.updateDescription(42, "x"));
        assertThrows(TaskNotFoundException.class, () -> service.changeStatus(42, TaskStatus.DONE));
        assertThrows(TaskNotFoundException.class, () -> service.delete(42));
    }

    @Test
    void deleteRemovesTask() {
        Task task = service.add("Temporary");

        service.delete(task.id());

        assertTrue(service.list(Optional.empty()).isEmpty());
    }

    @Test
    void listFiltersByStatus() {
        service.add("A");
        Task b = service.add("B");
        service.add("C");
        service.changeStatus(b.id(), TaskStatus.DONE);

        List<Task> done = service.list(Optional.of(TaskStatus.DONE));
        List<Task> todo = service.list(Optional.of(TaskStatus.TODO));

        assertEquals(List.of(b.id()), done.stream().map(Task::id).toList());
        assertEquals(2, todo.size());
        assertEquals(3, service.list(Optional.empty()).size());
    }

    @Test
    void statisticsCountsEveryStatus() {
        service.add("A");
        service.changeStatus(service.add("B").id(), TaskStatus.DONE);

        Map<TaskStatus, Long> stats = service.statistics();

        assertEquals(1L, stats.get(TaskStatus.TODO));
        assertEquals(0L, stats.get(TaskStatus.IN_PROGRESS));
        assertEquals(1L, stats.get(TaskStatus.DONE));
    }

    @Test
    void blankDescriptionIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> service.add("   "));
    }
}
