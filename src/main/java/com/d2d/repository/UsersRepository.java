package com.d2d.repository;

import com.d2d.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users,Long> {

    Optional<Users> findByUsername(String email);

    @Query(value = "select a From Users as a where a.email = :email")
    Optional<Users> findUserName(@Param("email") String email);

    @Query(value = "select a From Users as a where a.username = :username")
    Optional<Users> findUserNames(@Param("username") String username);

    Optional<Users> findByEmail(String email);

    @Query(value = "select a From Users as a where a.username = :pass")
    Optional<Users> findByPass(String pass);

}

