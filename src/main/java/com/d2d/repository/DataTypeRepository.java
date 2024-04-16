package com.d2d.repository;

import com.d2d.entity.DataType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DataTypeRepository extends JpaRepository<DataType, Integer> {


    List<DataType> findBydataBaseEngineIdAndIsActive(Integer dataBaseEngineId, Integer i);
}
