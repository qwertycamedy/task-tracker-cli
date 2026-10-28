package com.example.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DateUtilsTest {
    @Test
    void parseDateShouldReadExpectedFormat() {
        assertEquals(LocalDate.of(2026, 10, 28), DateUtils.parseDate("28.10.2026"));
    }

    @Test
    void parseDateShouldReturnNullForEmptyInput() {
        assertNull(DateUtils.parseDate(null));
        assertNull(DateUtils.parseDate(""));
        assertNull(DateUtils.parseDate("   "));
    }

    @Test
    void parseDateShouldReturnNullForInvalidInput() {
        assertNull(DateUtils.parseDate("2026-10-28"));
        assertNull(DateUtils.parseDate("32.10.2026"));
    }

    @Test
    void formatDateShouldUseExpectedFormat() {
        assertEquals("28.10.2026", DateUtils.formatDate(LocalDate.of(2026, 10, 28)));
    }

    @Test
    void formatDateShouldReturnNullForNullDate() {
        assertNull(DateUtils.formatDate(null));
    }
}
