package com.microservices.authendicationservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservices.authendicationservice.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

//	@Modifying
//    @Query("update Users set isDelete=true where email =?1")
//	boolean deleteByEmail(String email);
}
