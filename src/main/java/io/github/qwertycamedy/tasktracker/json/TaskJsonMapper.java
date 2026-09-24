package io.github.qwertycamedy.tasktracker.json;

import io.github.qwertycamedy.tasktracker.model.Task;
import io.github.qwertycamedy.tasktracker.model.TaskStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Converts tasks to and from their JSON representation. */
public final class TaskJsonMapper {

    private static final String ID = "id";
    private static final String DESCRIPTION = "description";
    private static final String STATUS = "status";
    private static final String CREATED_AT = "createdAt";
    private static final String UPDATED_AT = "updatedAt";

    private TaskJsonMapper() {
    }

    public static String toJson(List<Task> tasks) {
        List<Map<String, Object>> objects = tasks.stream()
                .map(TaskJsonMapper::toMap)
                .toList();
        return JsonWriter.write(objects);
    }

    public static List<Task> fromJson(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        if (!(JsonParser.parse(json) instanceof List<?> items)) {
            throw new JsonException("Root element must be an array of tasks");
        }
        return items.stream()
                .map(TaskJsonMapper::fromObject)
                .toList();
    }

    private static Map<String, Object> toMap(Task task) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(ID, task.id());
        map.put(DESCRIPTION, task.description());
        map.put(STATUS, task.status().value());
        map.put(CREATED_AT, task.createdAt().toString());
        map.put(UPDATED_AT, task.updatedAt().toString());
        return map;
    }

    private static Task fromObject(Object item) {
        if (!(item instanceof Map<?, ?> map)) {
            throw new JsonException("Each task must be a JSON object");
        }
        try {
            return new Task(
                    requireType(map, ID, Number.class).intValue(),
                    requireType(map, DESCRIPTION, String.class),
                    TaskStatus.fromValue(requireType(map, STATUS, String.class)),
                    LocalDateTime.parse(requireType(map, CREATED_AT, String.class)),
                    LocalDateTime.parse(requireType(map, UPDATED_AT, String.class))
            );
        } catch (DateTimeParseException | IllegalArgumentException e) {
            throw new JsonException("Invalid task data: " + e.getMessage());
        }
    }

    private static <T> T requireType(Map<?, ?> map, String key, Class<T> type) {
        Object value = map.get(key);
        if (!type.isInstance(value)) {
            throw new JsonException("Field '" + key + "' must be of type " + type.getSimpleName());
        }
        return type.cast(value);
    }
}
