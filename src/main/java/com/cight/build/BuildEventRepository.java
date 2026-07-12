package com.cight.build;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BuildEventRepository extends JpaRepository<BuildEvent, UUID> {

    Page<BuildEvent> findByRepoName(String repoName, Pageable pageable);

    Page<BuildEvent> findByStatus(BuildStatus status, Pageable pageable);

    Page<BuildEvent> findByRepoNameAndStatus(String repoName, BuildStatus status, Pageable pageable);

    List<BuildEvent> findTop20ByRepoNameAndStatusOrderByCreatedAtDesc(
            String repoName,
            BuildStatus status
    );

    boolean existsByGithubDeliveryId(String githubDeliveryId);

    Optional<BuildEvent> findByGithubRunId(Long githubRunId);

    @Query("""
            select count(b) from BuildEvent b
            where b.repoName = :repoName
              and b.status = :status
              and b.createdAt >= :since
            """)
    long countByRepoStatusSince(
            @Param("repoName") String repoName,
            @Param("status") BuildStatus status,
            @Param("since") Instant since
    );
}
