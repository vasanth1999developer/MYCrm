package com.clinch.crm.authentication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clinch.crm.authentication.models.User;

/**
 * Spring Data repository for {@link User}.
 *
 * <p>Spring generates the implementation at startup. Besides the inherited CRUD methods
 * ({@code save}, {@code findById}…), the methods below are <i>derived queries</i>: Spring builds
 * the SQL from the method name.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	/**
	 * Used at sign-in to load the user by login key.
	 *
	 * @param email the email
	 * @return the user, or empty if none has this email
	 */
	Optional<User> findByEmail(String email);

	/**
	 * Currently unused.
	 *
	 * @param username the username
	 * @return {@code true} if a user has this username
	 */
	Boolean existsByUsername(String username);

	/**
	 * Used by {@code POST /auth/users} and {@code GET /auth/signupcheckEmailId/{email}}.
	 *
	 * @param email the email
	 * @return {@code true} if a user has this email
	 */
	Boolean existsByEmail(String email);

//	@Modifying
//    @Query("update Users set isDelete=true where email =?1")
//	boolean deleteByEmail(String email);
}
