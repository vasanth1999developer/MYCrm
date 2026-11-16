package com.microservices.authendicationservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservices.authendicationservice.models.ERole;
import com.microservices.authendicationservice.models.Role;

/**
 * Legacy Spring Data repository for the unused {@link Role} entity. Injected into
 * {@code AuthController} but never called.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
	/**
	 * Derived query: {@code SELECT * FROM roles WHERE name = ?}.
	 *
	 * @param name the role name
	 * @return the role, or empty if none exists
	 */
	Optional<Role> findByName(ERole name);
}
