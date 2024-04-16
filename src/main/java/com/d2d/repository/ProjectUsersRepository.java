package com.d2d.repository;

import com.d2d.entity.ProjectUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectUsersRepository extends JpaRepository<ProjectUsers, Integer> {
    Optional<ProjectUsers> findByProjectIdAndDeletedFlag(Integer projectId, boolean b);
}
