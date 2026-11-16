package com.clinch.crm.authentication.services;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import  com.clinch.crm.authentication.models.User;
import com.clinch.crm.authentication.repository.UserRepository;



/**
 * Loads users for Spring Security's password check.
 *
 * <p>Spring's interface calls the login key "username", but in MyCrm the login key is the
 * <b>email</b>. That's why {@code POST /auth/signin} expects the email in its {@code username} field.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	/** Looks users up by email. */
	@Autowired
	UserRepository userRepository;

	/**
	 * Finds the user by email and wraps it as a Spring Security {@link UserDetails}.
	 *
	 * <p>Called by {@code DaoAuthenticationProvider} during sign-in, which then compares the
	 * password hash. {@code @Transactional} keeps the database session open while the roles
	 * collection is read.
	 *
	 * @param userEmail the email the user typed (Spring passes it as the "username")
	 * @return the user with its roles as authorities
	 * @throws UsernameNotFoundException if no user has this email; Spring reports it as bad
	 *         credentials (401), so callers can't tell an unknown email from a wrong password
	 */
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {

		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

		return UserDetailsImpl.build(user);
	}

}
