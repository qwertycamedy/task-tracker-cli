package io.github.qwertycamedy.tasktracker.repository;

import io.github.qwertycamedy.tasktracker.exception.StorageException;
import io.github.qwertycamedy.tasktracker.json.JsonException;
import io.github.qwertycamedy.tasktracker.json.TaskJsonMapper;
import io.github.qwertycamedy.tasktracker.model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stores tasks in a JSON file. The file is created on the first write.
 * Tasks are cached in a {@link TreeMap} so they are always sorted by id.
 * Writes go to a temporary file first and are then moved into place,
 * so a crash in the middle of a write never corrupts the data file.
 */
public class JsonFileTaskRepository implements Repository<Task, Integer> {

    private static final Logger LOG = Logger.getLogger(JsonFileTaskRepository.class.getName());

    private final Path file;
    private Map<Integer, Task> cache;

    public JsonFileTaskRepository(Path file) {
        this.file = file;
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks().values());
    }

    @Override
    public Optional<Task> findById(Integer id) {
        return Optional.ofNullable(tasks().get(id));
    }

    @Override
    public Task save(Task task) {
        tasks().put(task.id(), task);
        persist();
        return task;
    }

    @Override
    public boolean deleteById(Integer id) {
        boolean removed = tasks().remove(id) != null;
        if (removed) {
            persist();
        }
        return removed;
    }

    @Override
    public Integer nextId() {
        return tasks().keySet().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0) + 1;
    }

    private Map<Integer, Task> tasks() {
        if (cache == null) {
            cache = load();
        }
        return cache;
    }

    private Map<Integer, Task> load() {
        Map<Integer, Task> result = new TreeMap<>();
        if (Files.notExists(file)) {
            LOG.fine(() -> "Storage file " + file + " does not exist yet, starting empty");
            return result;
        }
        try {
            String json = Files.readString(file, StandardCharsets.UTF_8);
            for (Task task : TaskJsonMapper.fromJson(json)) {
                result.put(task.id(), task);
            }
            LOG.fine(() -> "Loaded " + result.size() + " task(s) from " + file);
            return result;
        } catch (IOException e) {
            throw new StorageException("Cannot read " + file + ": " + e.getMessage(), e);
        } catch (JsonException e) {
            throw new StorageException("File " + file + " is corrupted: " + e.getMessage(), e);
        }
    }

    private void persist() {
        String json = TaskJsonMapper.toJson(new ArrayList<>(cache.values())) + System.lineSeparator();
        Path absolute = file.toAbsolutePath();
        Path temp = absolute.resolveSibling(absolute.getFileName() + ".tmp");
        try {
            Files.writeString(temp, json, StandardCharsets.UTF_8);
            moveIntoPlace(temp, absolute);
            LOG.fine(() -> "Saved " + cache.size() + " task(s) to " + file);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to write " + file, e);
            throw new StorageException("Cannot write " + file + ": " + e.getMessage(), e);
        }
    }

    private static void moveIntoPlace(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
