package io.github.qwertycamedy.tasktracker.cli;

import io.github.qwertycamedy.tasktracker.exception.InvalidCommandException;
import io.github.qwertycamedy.tasktracker.exception.TaskTrackerException;
import io.github.qwertycamedy.tasktracker.model.Task;
import io.github.qwertycamedy.tasktracker.model.TaskStatus;
import io.github.qwertycamedy.tasktracker.service.TaskService;

import java.io.PrintStream;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Parses command-line arguments and delegates to {@link TaskService}.
 * Commands are stored in a map, so adding a new one means adding one
 * {@link #register} call instead of growing a big switch statement.
 */
public class TaskCli {

    public static final int EXIT_OK = 0;
    public static final int EXIT_ERROR = 1;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int MAX_DESCRIPTION_WIDTH = 50;

    private final TaskService service;
    private final PrintStream out;
    private final PrintStream err;
    private final Map<String, CommandSpec> commands = new LinkedHashMap<>();

    public TaskCli(TaskService service, PrintStream out, PrintStream err) {
        this.service = service;
        this.out = out;
        this.err = err;
        registerCommands();
    }

    /** Runs one command and returns the process exit code. */
    public int run(String[] args) {
        if (args.length == 0) {
            printHelp();
            return EXIT_ERROR;
        }
        String name = args[0].toLowerCase();
        List<String> rest = Arrays.asList(args).subList(1, args.length);
        try {
            CommandSpec spec = Optional.ofNullable(commands.get(name))
                    .orElseThrow(() -> new InvalidCommandException(
                            "Unknown command '" + args[0] + "'. Run 'help' to see available commands."));
            spec.handler().execute(rest);
            return EXIT_OK;
        } catch (TaskTrackerException e) {
            err.println("Error: " + e.getMessage());
            return EXIT_ERROR;
        }
    }

    private void registerCommands() {
        register("add", "add \"<description>\"", "Add a new task", this::add);
        register("update", "update <id> \"<description>\"", "Change a task description", this::update);
        register("delete", "delete <id>", "Delete a task", this::delete);
        register("mark-in-progress", "mark-in-progress <id>", "Mark a task as in progress",
                args -> markStatus(args, TaskStatus.IN_PROGRESS));
        register("mark-done", "mark-done <id>", "Mark a task as done",
                args -> markStatus(args, TaskStatus.DONE));
        register("mark-todo", "mark-todo <id>", "Move a task back to todo",
                args -> markStatus(args, TaskStatus.TODO));
        register("list", "list [todo|in-progress|done]", "List all tasks or filter by status", this::list);
        register("stats", "stats", "Show the number of tasks per status", args -> stats());
        register("help", "help", "Show this help", args -> printHelp());
    }

    private void register(String name, String usage, String description, Command handler) {
        commands.put(name, new CommandSpec(name, usage, description, handler));
    }

    private void add(List<String> args) {
        requireArgCount(args, 1, "add");
        Task task = service.add(requireText(args.get(0)));
        out.println("Task added successfully (ID: " + task.id() + ")");
    }

    private void update(List<String> args) {
        requireArgCount(args, 2, "update");
        Task task = service.updateDescription(parseId(args.get(0)), requireText(args.get(1)));
        out.println("Task " + task.id() + " updated");
    }

    private void delete(List<String> args) {
        requireArgCount(args, 1, "delete");
        int id = parseId(args.get(0));
        service.delete(id);
        out.println("Task " + id + " deleted");
    }

    private void markStatus(List<String> args, TaskStatus status) {
        requireArgCount(args, 1, "mark-" + status.value());
        Task task = service.changeStatus(parseId(args.get(0)), status);
        out.println("Task " + task.id() + " marked as " + status);
    }

    private void list(List<String> args) {
        if (args.size() > 1) {
            throw usageError("list");
        }
        Optional<TaskStatus> filter;
        try {
            filter = args.stream().findFirst().map(TaskStatus::fromValue);
        } catch (IllegalArgumentException e) {
            throw new InvalidCommandException(e.getMessage());
        }
        List<Task> tasks = service.list(filter);
        if (tasks.isEmpty()) {
            out.println(filter.map(s -> "No tasks with status '" + s + "'").orElse("No tasks yet"));
            return;
        }
        printTable(tasks);
    }

    private void stats() {
        Map<TaskStatus, Long> stats = service.statistics();
        long total = stats.values().stream().mapToLong(Long::longValue).sum();
        stats.forEach((status, count) -> out.printf("%-12s %d%n", status + ":", count));
        out.printf("%-12s %d%n", "total:", total);
    }

    private void printTable(List<Task> tasks) {
        String format = "%-4s %-12s %-" + MAX_DESCRIPTION_WIDTH + "s %-16s %s%n";
        out.printf(format, "ID", "STATUS", "DESCRIPTION", "CREATED", "UPDATED");
        for (Task task : tasks) {
            out.printf(format,
                    task.id(),
                    task.status(),
                    truncate(task.description()),
                    task.createdAt().format(DATE_FORMAT),
                    task.updatedAt().format(DATE_FORMAT));
        }
    }

    private void printHelp() {
        out.println("Task Tracker CLI - manage your tasks from the terminal");
        out.println();
        out.println("Usage: task-cli <command> [arguments]");
        out.println();
        int width = commands.values().stream()
                .mapToInt(spec -> spec.usage().length())
                .max()
                .orElse(0);
        commands.values().forEach(spec ->
                out.printf("  %-" + width + "s  %s%n", spec.usage(), spec.description()));
    }

    private void requireArgCount(List<String> args, int expected, String command) {
        if (args.size() != expected) {
            throw usageError(command);
        }
    }

    private InvalidCommandException usageError(String command) {
        String usage = Optional.ofNullable(commands.get(command))
                .map(CommandSpec::usage)
                .orElse(command);
        return new InvalidCommandException("Wrong arguments. Usage: task-cli " + usage);
    }

    private static int parseId(String raw) {
        try {
            int id = Integer.parseInt(raw);
            if (id <= 0) {
                throw new NumberFormatException();
            }
            return id;
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Task id must be a positive number, got '" + raw + "'");
        }
    }

    private static String requireText(String text) {
        if (text.isBlank()) {
            throw new InvalidCommandException("Description must not be empty");
        }
        return text;
    }

    private static String truncate(String text) {
        String singleLine = text.replaceAll("\\s+", " ");
        return singleLine.length() <= MAX_DESCRIPTION_WIDTH
                ? singleLine
                : singleLine.substring(0, MAX_DESCRIPTION_WIDTH - 3) + "...";
    }
}
