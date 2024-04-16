package com.d2d.repository;

import com.d2d.entity.ProjectFeatures;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectFeaturesRepository extends JpaRepository<ProjectFeatures,Integer> {

    List<ProjectFeatures> findAllByDeletedFlag(boolean b);
}
