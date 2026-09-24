package io.github.qwertycamedy.tasktracker.cli;

/** Metadata about a command, used for dispatching and for the help screen. */
public record CommandSpec(String name, String usage, String description, Command handler) {
}
