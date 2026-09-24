package io.github.qwertycamedy.tasktracker.cli;

import java.util.List;

/** A single CLI command handler; receives the arguments after the command name. */
@FunctionalInterface
public interface Command {

    void execute(List<String> args);
}
