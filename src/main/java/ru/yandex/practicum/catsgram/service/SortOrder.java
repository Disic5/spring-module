package ru.yandex.practicum.catsgram.service;

public enum SortOrder {
    ASCENDING, DESCENDING;

    public static SortOrder from(String sort) {
        return switch (sort) {
            case "ascending", "asc" -> ASCENDING;
            case "descending", "desc" -> DESCENDING;
            default -> null;
        };
    }
}
