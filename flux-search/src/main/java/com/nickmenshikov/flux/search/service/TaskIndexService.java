package com.nickmenshikov.flux.search.service;

import com.nickmenshikov.flux.contracts.TaskEvent;
import com.nickmenshikov.flux.search.document.TaskDocument;
import com.nickmenshikov.flux.search.repository.TaskSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskIndexService {

    private final TaskSearchRepository repository;

    public void index(TaskEvent event) {
        repository.save(new TaskDocument(
                event.taskId().toString(),
                event.taskId(),
                event.userId(),
                event.title(),
                event.description(),
                event.status().toString(),
                event.priority().toString(),
                event.createdAt()
        ));
    }

    public void delete(Long taskId) {
        repository.deleteById(taskId.toString());
    }
}
