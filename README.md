# Task Tracker CLI

A command-line application for tracking tasks, written in plain **Java 17** with **no third-party runtime libraries**. Tasks are stored in a local JSON file that the app reads and writes with its own JSON parser and serializer.

Project idea: [roadmap.sh — Task Tracker](https://roadmap.sh/projects/task-tracker)

## Features

- Add, update and delete tasks
- Mark tasks as `todo`, `in-progress` or `done`
- List all tasks or filter them by status
- Show statistics per status
- Data is saved to `tasks.json` (created automatically)
- Crash-safe writes: data goes to a temp file first and is then atomically moved into place
- Clear error messages and non-zero exit codes for invalid input

## Tech stack

| Area | Used |
|---|---|
| Language | Java 17 (records, switch expressions, pattern matching for `instanceof`) |
| Core APIs | Collections, Generics, Stream API, `java.nio.file`, `java.time`, `java.util.logging` |
| Build | Maven |
| Tests | JUnit 5 (unit + parameterized tests, `@TempDir`) |

## Getting started

Requirements: JDK 17+ and Maven.

```bash
git clone https://github.com/qwertycamedy/task-tracker-cli.git
cd task-tracker-cli
mvn clean package
java -jar target/task-tracker-cli.jar help
```

Optional alias so you can type `task-cli` instead:

```bash
alias task-cli='java -jar /path/to/task-tracker-cli/target/task-tracker-cli.jar'
```

## Usage

```bash
task-cli add "Buy groceries"
# Task added successfully (ID: 1)

task-cli update 1 "Buy groceries and cook dinner"
task-cli mark-in-progress 1
task-cli mark-done 1
task-cli mark-todo 1
task-cli delete 1

task-cli list
task-cli list todo
task-cli list in-progress
task-cli list done

task-cli stats
task-cli help
```

Example output of `task-cli list`:

```
ID   STATUS       DESCRIPTION                                        CREATED          UPDATED
1    done         Buy groceries                                      2026-09-24 13:05 2026-09-24 13:05
2    in-progress  Prepare for interview                              2026-09-24 13:05 2026-09-24 13:10
3    todo         Finish Spring REST tutorial                        2026-09-24 13:05 2026-09-24 13:05
```

### Configuration

| Environment variable | Default | Description |
|---|---|---|
| `TASK_TRACKER_FILE` | `tasks.json` in the current directory | Path to the storage file |
| `TASK_TRACKER_DEBUG` | `false` | Set to `true` to print debug logs |

## Task format

```json
[
  {
    "id": 1,
    "description": "Buy groceries",
    "status": "todo",
    "createdAt": "2026-09-24T13:05:49",
    "updatedAt": "2026-09-24T13:05:49"
  }
]
```

## Architecture

The code is split into layers, each with one responsibility:

```
src/main/java/io/github/qwertycamedy/tasktracker
├── TaskTrackerApp.java          entry point, wires the layers together
├── cli/                         argument parsing, command dispatch, console output
├── service/TaskService.java     business logic
├── repository/                  generic Repository<T, ID> + JSON file implementation
├── json/                        hand-written JSON parser, writer and Task mapper
├── model/                       Task (immutable record) and TaskStatus (enum)
└── exception/                   custom exception hierarchy
```

Key design decisions:

- **`Repository<T, ID>` interface.** `TaskService` depends on the abstraction, not on the file. Unit tests use an in-memory implementation, and the storage could be replaced with a database without touching business logic.
- **Immutable `Task` record.** Changes create a new copy via `withStatus(...)` / `withDescription(...)`, which avoids accidental shared-state bugs.
- **Command map instead of a large `switch`.** Each command is registered once with its usage text, so the help screen is generated automatically and adding a command is a one-line change.
- **Injected `Clock`.** Timestamps are deterministic in tests.
- **Exception hierarchy.** All expected errors extend `TaskTrackerException`; the CLI catches them and prints a readable message instead of a stack trace.

## Running tests

```bash
mvn test
```

The test suite covers the service logic, JSON round-trips (including escaping and Unicode), file persistence and corrupted-file handling, and CLI error cases.
