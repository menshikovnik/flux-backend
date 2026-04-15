package com.nickmenshikov.flux.tasks.repository;

import com.nickmenshikov.flux.core.model.Priority;
import com.nickmenshikov.flux.core.model.Status;
import com.nickmenshikov.flux.core.model.Task;
import com.nickmenshikov.flux.core.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    Optional<Task> findTaskByIdAndCreator(Long id, User creator);

    void deleteTaskById(Long id);

    @Query("SELECT t FROM Task t WHERE t.creator = :creator " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND (:priority IS NULL OR t.priority = :priority)" +
            "AND (:projectId IS NULL OR t.project.id = :projectId)")
    Page<Task> findAllFiltered(
            @Param("creator") User creator,
            @Param("status") Status status,
            @Param("priority") Priority priority,
            Pageable pageable,
            @Param("projectId") Long projectId
    );
}
