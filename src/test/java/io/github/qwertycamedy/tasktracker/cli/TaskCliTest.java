package io.github.qwertycamedy.tasktracker.cli;

import io.github.qwertycamedy.tasktracker.repository.InMemoryTaskRepository;
import io.github.qwertycamedy.tasktracker.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskCliTest {

    private ByteArrayOutputStream out;
    private ByteArrayOutputStream err;
    private TaskCli cli;

    @BeforeEach
    void setUp() {
        out = new ByteArrayOutputStream();
        err = new ByteArrayOutputStream();
        TaskService service = new TaskService(new InMemoryTaskRepository(), Clock.systemUTC());
        cli = new TaskCli(service,
                new PrintStream(out, true, StandardCharsets.UTF_8),
                new PrintStream(err, true, StandardCharsets.UTF_8));
    }

    @Test
    void addAndListTasks() {
        assertEquals(TaskCli.EXIT_OK, cli.run(new String[]{"add", "Learn Java"}));
        assertEquals(TaskCli.EXIT_OK, cli.run(new String[]{"list"}));

        String output = out.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Task added successfully (ID: 1)"));
        assertTrue(output.contains("Learn Java"));
    }

    @Test
    void unknownCommandReturnsError() {
        assertEquals(TaskCli.EXIT_ERROR, cli.run(new String[]{"fly"}));
        assertTrue(err.toString(StandardCharsets.UTF_8).contains("Unknown command"));
    }

    @Test
    void invalidIdReturnsError() {
        assertEquals(TaskCli.EXIT_ERROR, cli.run(new String[]{"delete", "abc"}));
        assertTrue(err.toString(StandardCharsets.UTF_8).contains("positive number"));
    }

    @Test
    void missingTaskReturnsError() {
        assertEquals(TaskCli.EXIT_ERROR, cli.run(new String[]{"mark-done", "5"}));
        assertTrue(err.toString(StandardCharsets.UTF_8).contains("not found"));
    }

    @Test
    void invalidStatusFilterReturnsError() {
        assertEquals(TaskCli.EXIT_ERROR, cli.run(new String[]{"list", "later"}));
        assertTrue(err.toString(StandardCharsets.UTF_8).contains("Unknown status"));
    }
}
