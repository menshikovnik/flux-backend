package com.nickmenshikov.flux.projects.repository;

import com.nickmenshikov.flux.core.model.Project;
import com.nickmenshikov.flux.core.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findProjectByNameAndUser(String name, User user);

    @Query("SELECT p FROM Project p WHERE p.user = :user AND (:archived IS NULL OR p.isArchived = :archived)")
    Page<Project> findAllByOwner(@Param("user") User owner, @Param("archived") Boolean archived, Pageable pageable);

    Optional<Project> findProjectByIdAndUser(Long id, User user);

    void deleteProjectById(Long id);
}
