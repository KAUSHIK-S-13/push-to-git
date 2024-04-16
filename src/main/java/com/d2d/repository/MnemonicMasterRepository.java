package com.d2d.repository;

import com.d2d.entity.MnemonicMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MnemonicMasterRepository extends JpaRepository<MnemonicMaster, Integer> {

	@Query("SELECT o FROM MnemonicMaster o ")
	List<MnemonicMaster> findAllList();


	@Query("SELECT a FROM MnemonicMaster a WHERE  a.mnemonicType.typeName= :masterName AND " +
			"a.deletedFlag = false ORDER BY a.sortOrder ASC")
	List<MnemonicMaster> findByMnemonicName(String masterName);

	@Query("SELECT a FROM MnemonicMaster a WHERE  a.mnemonicType.id = :typeId  AND " +
			"a.deletedFlag = false ORDER BY a.sortOrder ASC")
	List<MnemonicMaster> findByTypeId(Integer typeId);
}

