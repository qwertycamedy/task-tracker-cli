package com.example.repository;

import java.time.LocalDate;
import java.util.List;

import com.example.model.EPriority;
import com.example.model.EStatus;

public interface ITaskRepository<T> {
    void add(T task);

    T getById(int taskId);

    void update(T task);

    boolean removeById(int taskId);

    List<T> getAll();

    List<T> getByPriority(EPriority priority);

    List<T> getByStatus(EStatus status);

    List<T> getByDeadline(LocalDate deadline);
}
