package io.github.qwertycamedy.tasktracker.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic CRUD storage abstraction.
 *
 * @param <T>  entity type
 * @param <ID> identifier type
 */
public interface Repository<T, ID> {

    List<T> findAll();

    Optional<T> findById(ID id);

    /** Inserts a new entity or replaces an existing one with the same id. */
    T save(T entity);

    /** @return {@code true} if an entity was removed */
    boolean deleteById(ID id);

    /** @return the next free identifier */
    ID nextId();
}
