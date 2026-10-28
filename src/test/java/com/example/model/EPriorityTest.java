package com.example.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class EPriorityTest {
    @Test
    void fromLabelShouldIgnoreCaseAndSurroundingSpaces() {
        assertEquals(EPriority.LOW, EPriority.fromLabel(" LOW "));
        assertEquals(EPriority.MEDIUM, EPriority.fromLabel("Medium"));
        assertEquals(EPriority.HIGH, EPriority.fromLabel("high"));
    }

    @Test
    void fromLabelShouldReturnNullForUnknownValue() {
        assertNull(EPriority.fromLabel("urgent"));
        assertNull(EPriority.fromLabel(""));
    }

    @Test
    void getLabelShouldReturnReadableValue() {
        assertEquals("low", EPriority.LOW.getLabel());
        assertEquals("medium", EPriority.MEDIUM.getLabel());
        assertEquals("high", EPriority.HIGH.getLabel());
    }
}
