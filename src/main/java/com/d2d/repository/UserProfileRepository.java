package com.d2d.repository;

import com.d2d.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile,Integer> {

    @Query("select p from UserProfile as p where createdBy=:id ")
    Optional<UserProfile> findByIdCreatedBy(Long id);
}
