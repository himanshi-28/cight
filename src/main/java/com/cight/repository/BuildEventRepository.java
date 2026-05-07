package com.cight.repository;

import com.cight.model.BuildEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BuildEventRepository
        extends JpaRepository<BuildEvent, String> {

    // find all builds for a specific repo
    List<BuildEvent> findByRepoName(String repoName);

    // find all builds with a specific status
    List<BuildEvent> findByStatus(String status);

    // find all builds for a repo, newest first
    List<BuildEvent> findByRepoNameOrderByCreatedAtDesc(String repoName);
}