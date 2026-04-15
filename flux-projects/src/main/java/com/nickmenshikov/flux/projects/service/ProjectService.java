package com.nickmenshikov.flux.projects.service;

import com.nickmenshikov.flux.core.dto.CreateProjectRequest;
import com.nickmenshikov.flux.core.exception.ProjectNameAlreadyTakenException;
import com.nickmenshikov.flux.core.exception.ProjectNotFoundException;
import com.nickmenshikov.flux.core.model.Project;
import com.nickmenshikov.flux.core.model.User;
import com.nickmenshikov.flux.projects.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public Project create(CreateProjectRequest request, User user) {
        projectRepository.findProjectByNameAndUser(request.name(), user)
                .ifPresent(
                        _ -> {
                            throw new ProjectNameAlreadyTakenException("Project name already taken: " + request.name());
                        }
                );

        Project project = new Project();

        project.setUser(user);
        project.setName(request.name());
        project.setDescription(request.description());
        project.setColor(request.color());
        project.setIsArchived(false);
        project.setCreatedAt(Instant.now());

        return projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public Page<Project> getAllProject(User user, Boolean isArchived, Pageable pageable) {
        return projectRepository.findAllByOwner(user, isArchived, pageable);
    }

    @Transactional
    public void delete(Long id, User user) {
        Project project = projectRepository.findProjectByIdAndUser(id, user)
                .orElseThrow(
                        () -> new ProjectNotFoundException("Project not found: " + id)
                );

        projectRepository.deleteProjectById(project.getId());
    }

    @Transactional(readOnly = true)
    public Project getById(Long id, User user) {
        return projectRepository.findProjectByIdAndUser(id, user)
                .orElseThrow(
                        () -> new ProjectNotFoundException("Project not found: " + id)
                );
    }
}
