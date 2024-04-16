package com.d2d.repository;

import com.d2d.entity.DataBaseEngine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataBaseEngineRepository extends JpaRepository<DataBaseEngine, Integer> {

    List<DataBaseEngine> findByIsActive(int i);

    Optional<DataBaseEngine> findByIdAndIsActive(Integer databaseEngineId, int i);
}
