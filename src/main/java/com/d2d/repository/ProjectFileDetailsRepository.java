package com.d2d.repository;

import com.d2d.entity.ProjectFileDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectFileDetailsRepository extends JpaRepository<ProjectFileDetails, Integer> {
    Optional<ProjectFileDetails> findByProjectIdAndDeletedFlag(Integer projectId, boolean b);
}
