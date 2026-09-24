package io.github.qwertycamedy.tasktracker.json;

import io.github.qwertycamedy.tasktracker.model.Task;
import io.github.qwertycamedy.tasktracker.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskJsonMapperTest {

    private static final LocalDateTime TIME = LocalDateTime.of(2026, 3, 1, 12, 30, 15);

    @Test
    void roundTripKeepsAllFields() {
        List<Task> tasks = List.of(
                new Task(1, "Simple", TaskStatus.TODO, TIME, TIME),
                new Task(2, "Quotes \"inside\", back\\slash\nnew line, unicode: Қазақстан",
                        TaskStatus.IN_PROGRESS, TIME, TIME.plusHours(1)));

        List<Task> restored = TaskJsonMapper.fromJson(TaskJsonMapper.toJson(tasks));

        assertEquals(tasks, restored);
    }

    @Test
    void emptyInputGivesEmptyList() {
        assertTrue(TaskJsonMapper.fromJson("").isEmpty());
        assertTrue(TaskJsonMapper.fromJson("[]").isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{broken",
            "{\"id\": 1}",
            "[{\"id\": \"one\"}]",
            "[1, 2, 3]",
            "[] trailing"
    })
    void invalidJsonIsRejected(String json) {
        assertThrows(JsonException.class, () -> TaskJsonMapper.fromJson(json));
    }

    @Test
    void parserHandlesNestedStructures() {
        Object parsed = JsonParser.parse("{\"a\": [1, 2.5, true, null], \"b\": {\"c\": \"\\u0041\"}}");

        Map<?, ?> root = (Map<?, ?>) parsed;
        assertEquals(List.of(1L, 2.5, true), ((List<?>) root.get("a")).subList(0, 3));
        assertEquals("A", ((Map<?, ?>) root.get("b")).get("c"));
    }
}
