package com.nickmenshikov.flux.projects.controller;

import com.nickmenshikov.flux.core.dto.CreateProjectRequest;
import com.nickmenshikov.flux.core.dto.ProjectResponse;
import com.nickmenshikov.flux.core.model.FluxUserDetails;
import com.nickmenshikov.flux.core.model.Project;
import com.nickmenshikov.flux.projects.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping(version = "1.0")
    public ResponseEntity<ProjectResponse> create(@RequestBody CreateProjectRequest request, @AuthenticationPrincipal FluxUserDetails userDetails) {
        Project project = projectService.create(request, userDetails.getUser());
        return ResponseEntity
                .created(URI.create("/api/projects/" + project.getId()))
                .body(ProjectResponse.from(project));
    }

    @GetMapping(version = "1.0")
    public ResponseEntity<Page<ProjectResponse>> getAll(
            @RequestParam(required = false) Boolean archived,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal FluxUserDetails userDetails
    ) {
        return ResponseEntity.ok(projectService.getAllProject(userDetails.getUser(), archived, pageable).map(ProjectResponse::from));
    }

    @GetMapping(value = "/{id}", version = "1.0")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id, @AuthenticationPrincipal FluxUserDetails userDetails) {
        return ResponseEntity.ok(ProjectResponse.from(projectService.getById(id, userDetails.getUser())));
    }

    @DeleteMapping(value = "/{id}", version = "1.0")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal FluxUserDetails userDetails) {
        projectService.delete(id, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }
}
