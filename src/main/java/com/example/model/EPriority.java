package com.example.model;

public enum EPriority {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high");

    private final String label;

    EPriority(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public static EPriority fromLabel(String input) {
        for (EPriority s : values()) {
            if (s.label.equalsIgnoreCase(input.trim())) {
                return s;
            }
        }

        return null;
    }
}
