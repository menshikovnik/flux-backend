package com.nickmenshikov.flux.search.repository;

import com.nickmenshikov.flux.search.document.TaskDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskSearchRepository extends ElasticsearchRepository<TaskDocument, String> {
    List<TaskDocument> findByUserId(Long userId);
    List<TaskDocument> findByUserIdAndTitleContaining(Long userId, String title);
}
