package com.example.model;

public enum EStatus {
    TODO("to-do"),
    PROGRESS("in progress"),
    COMPLETED("completed");

    private final String label;

    EStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public static EStatus fromLabel(String input) {
        for (EStatus s : values()) {
            if (s.label.equalsIgnoreCase(input.trim())) {
                return s;
            }
        }

        return null;
    }
}
