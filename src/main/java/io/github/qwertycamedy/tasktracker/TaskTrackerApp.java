package io.github.qwertycamedy.tasktracker;

import io.github.qwertycamedy.tasktracker.cli.TaskCli;
import io.github.qwertycamedy.tasktracker.repository.JsonFileTaskRepository;
import io.github.qwertycamedy.tasktracker.service.TaskService;

import java.nio.file.Path;
import java.time.Clock;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application entry point: wires the layers together (manual dependency injection).
 * <p>
 * Configuration:
 * <ul>
 *   <li>{@code TASK_TRACKER_FILE} - path to the storage file (default: {@code tasks.json} in the current directory)</li>
 *   <li>{@code TASK_TRACKER_DEBUG=true} - print debug logs to stderr</li>
 * </ul>
 */
public final class TaskTrackerApp {

    private static final String DEFAULT_FILE = "tasks.json";

    private TaskTrackerApp() {
    }

    public static void main(String[] args) {
        configureLogging(Boolean.parseBoolean(System.getenv("TASK_TRACKER_DEBUG")));

        Path file = Path.of(System.getenv().getOrDefault("TASK_TRACKER_FILE", DEFAULT_FILE));
        TaskService service = new TaskService(new JsonFileTaskRepository(file), Clock.systemDefaultZone());
        TaskCli cli = new TaskCli(service, System.out, System.err);

        System.exit(cli.run(args));
    }

    private static void configureLogging(boolean debug) {
        Logger root = Logger.getLogger("io.github.qwertycamedy.tasktracker");
        root.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        Level level = debug ? Level.FINE : Level.WARNING;
        handler.setLevel(level);
        root.setLevel(level);
        root.addHandler(handler);
    }
}
