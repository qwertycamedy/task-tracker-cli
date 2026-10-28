package com.example.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    void constructorShouldTrimTitleAndKeepProvidedValues() {
        LocalDate today = LocalDate.now();
        LocalDate deadline = LocalDate.of(2026, 12, 5);

        Task task = new Task("  Finish project  ", EPriority.HIGH, EStatus.PROGRESS, deadline);

        assertEquals("Finish project", task.getTitle());
        assertEquals(EPriority.HIGH, task.getPriority());
        assertEquals(EStatus.PROGRESS, task.getStatus());
        assertEquals(today, task.getCreatedAt());
        assertEquals(deadline, task.getDeadline());
    }

    @Test
    void constructorShouldUseDefaultsForNullOptionalValues() {
        LocalDate today = LocalDate.now();

        Task task = new Task("Read a book", null, null, null);

        assertEquals(EPriority.LOW, task.getPriority());
        assertEquals(EStatus.TODO, task.getStatus());
        assertEquals(today, task.getCreatedAt());
        assertEquals(today.plusDays(10), task.getDeadline());
    }

    @Test
    void constructorShouldRejectNullTitle() {
        assertThrows(IllegalArgumentException.class,
                () -> new Task(null, EPriority.LOW, EStatus.TODO, LocalDate.now()));
    }

    @Test
    void constructorShouldRejectBlankTitle() {
        assertThrows(IllegalArgumentException.class,
                () -> new Task("   ", EPriority.LOW, EStatus.TODO, LocalDate.now()));
    }

    @Test
    void settersShouldUpdateTaskValues() {
        Task task = new Task("Old title", EPriority.LOW, EStatus.TODO, LocalDate.of(2026, 12, 5));
        LocalDate newDeadline = LocalDate.of(2027, 1, 10);

        task.setTitle("New title");
        task.setPriority(EPriority.HIGH);
        task.setStatus(EStatus.COMPLETED);
        task.setDeadline(newDeadline);

        assertEquals("New title", task.getTitle());
        assertEquals(EPriority.HIGH, task.getPriority());
        assertEquals(EStatus.COMPLETED, task.getStatus());
        assertEquals(newDeadline, task.getDeadline());
    }

    @Test
    void toStringShouldContainTaskDetails() {
        Task task = new Task("Finish project", EPriority.HIGH, EStatus.COMPLETED,
                LocalDate.of(2026, 12, 5));

        String expected = "#" + task.getTaskId()
                + " [high] - Finish project - completed - deadline: 05.12.2026";

        assertEquals(expected, task.toString());
    }
}