package com.example.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateUtils {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private DateUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated!");
    }

    public static String formatDate(LocalDate targetDateTime) {

        if (targetDateTime == null) {
            return null;
        }

        return targetDateTime.format(dateTimeFormatter);
    }

    public static LocalDate parseDate(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(input, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            System.out.println("Date time parse exception: " + e);
            return null;
        }
    }
}
