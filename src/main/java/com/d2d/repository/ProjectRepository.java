package com.d2d.repository;

import com.d2d.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Integer> {


    @Query("select DISTINCT p from Project as p " +
            "join ProjectUsers as ps on p.id=ps.project.id " +
            "where ps.user.id= :userId and p.dbEngine.id = :engineId and ps.deletedFlag=False ORDER By p.id DESC")
    List<Project> findAllByUserAndEngineId(Long userId, Integer engineId);

    Optional<Project> findByIdAndDeletedFlag(Integer projectId, boolean b);
}
