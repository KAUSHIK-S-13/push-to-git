package com.d2d.repository;

import com.d2d.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

	@Query("SELECT o FROM Role o ")
	List<Role> findAllList();
}

