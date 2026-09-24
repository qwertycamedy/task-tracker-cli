package io.github.qwertycamedy.tasktracker.repository;

import io.github.qwertycamedy.tasktracker.exception.StorageException;
import io.github.qwertycamedy.tasktracker.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonFileTaskRepositoryTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 1, 1, 9, 0);

    @TempDir
    Path tempDir;

    @Test
    void missingFileMeansEmptyRepository() {
        JsonFileTaskRepository repository = new JsonFileTaskRepository(tempDir.resolve("tasks.json"));

        assertTrue(repository.findAll().isEmpty());
        assertEquals(1, repository.nextId());
    }

    @Test
    void savedTasksSurviveReload() {
        Path file = tempDir.resolve("tasks.json");
        JsonFileTaskRepository first = new JsonFileTaskRepository(file);
        first.save(Task.create(1, "Persist me", NOW));
        first.save(Task.create(2, "Me too", NOW));

        JsonFileTaskRepository second = new JsonFileTaskRepository(file);

        assertEquals(2, second.findAll().size());
        assertEquals("Persist me", second.findById(1).orElseThrow().description());
        assertEquals(3, second.nextId());
    }

    @Test
    void deleteRemovesTaskFromFile() {
        Path file = tempDir.resolve("tasks.json");
        JsonFileTaskRepository repository = new JsonFileTaskRepository(file);
        repository.save(Task.create(1, "Delete me", NOW));

        assertTrue(repository.deleteById(1));
        assertFalse(repository.deleteById(1));
        assertTrue(new JsonFileTaskRepository(file).findAll().isEmpty());
    }

    @Test
    void corruptedFileThrowsStorageException() throws IOException {
        Path file = tempDir.resolve("tasks.json");
        Files.writeString(file, "not json at all");

        JsonFileTaskRepository repository = new JsonFileTaskRepository(file);

        assertThrows(StorageException.class, repository::findAll);
    }
}
