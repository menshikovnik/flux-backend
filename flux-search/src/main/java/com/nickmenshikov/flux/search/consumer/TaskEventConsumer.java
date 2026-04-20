package com.nickmenshikov.flux.search.consumer;

import com.nickmenshikov.flux.contracts.TaskEvent;
import com.nickmenshikov.flux.search.service.TaskIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskEventConsumer {

    private final TaskIndexService indexService;

    @KafkaListener(topics = "flux.tasks.events", groupId = "flux-search")
    public void consume(TaskEvent event) {
        switch (event.type()) {
            case CREATED, UPDATED -> indexService.index(event);
            case DELETED -> indexService.delete(event.taskId());
        }
    }
}
