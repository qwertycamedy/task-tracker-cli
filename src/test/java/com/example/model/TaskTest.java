package com.example.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class TaskTest {
    private final Task task = new Task("Test Task Title", null, null, null);

    @Test
    void shouldGetTaskTitle() {
        assertEquals("Test Task Title", task.getTitle());
    }

    @Test
    void shouldGetTaskPriority() {
        task.setPriority(EPriority.HIGH);
        assertEquals(EPriority.HIGH, task.getPriority());
    }

    @Test
    void shouldGetTaskIsCompleted() {
        task.setStatus(EStatus.COMPLETED);
        assertEquals(EStatus.COMPLETED, task.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenTitleNullOrBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Task(null,  null, null, null));
        assertThrows(IllegalArgumentException.class, () -> new Task("",  null, null, null));
        assertThrows(IllegalArgumentException.class, () -> new Task("   ",  null, null, null));
    }
}