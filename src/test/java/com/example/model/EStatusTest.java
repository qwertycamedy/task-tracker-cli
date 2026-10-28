package com.example.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class EStatusTest {
    @Test
    void fromLabelShouldIgnoreCaseAndSurroundingSpaces() {
        assertEquals(EStatus.TODO, EStatus.fromLabel(" TO-DO "));
        assertEquals(EStatus.PROGRESS, EStatus.fromLabel("In Progress"));
        assertEquals(EStatus.COMPLETED, EStatus.fromLabel("completed"));
    }

    @Test
    void fromLabelShouldReturnNullForUnknownValue() {
        assertNull(EStatus.fromLabel("started"));
        assertNull(EStatus.fromLabel(""));
    }

    @Test
    void getLabelShouldReturnReadableValue() {
        assertEquals("to-do", EStatus.TODO.getLabel());
        assertEquals("in progress", EStatus.PROGRESS.getLabel());
        assertEquals("completed", EStatus.COMPLETED.getLabel());
    }
}
