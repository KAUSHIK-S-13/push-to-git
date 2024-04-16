package com.d2d.repository;

import com.d2d.entity.MnemonicType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MnemonicTypeRepository extends JpaRepository<MnemonicType, Integer> {

	@Query("SELECT o FROM MnemonicType o ")
	List<MnemonicType> findAllList();
}

