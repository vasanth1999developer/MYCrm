package com.microservices.authendicationservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.microservices.authendicationservice.jwt.AuthEntryPointJwt;
import com.microservices.authendicationservice.services.UserDetailsServiceImpl;

/**
 * Spring Security setup for this service.
 *
 * <p>Its main job is to support <b>password login</b> ({@link #authenticationManager}), not to
 * protect endpoints: {@code /auth/**} is permitted here, and JWT checks happen in the gateway.
 * Direct calls that skip the gateway are blocked earlier by
 * {@link com.microservices.authendicationservice.gatewayauth.GatewayAuthFilter}.
 *
 * <p>{@code @EnableMethodSecurity} turns on {@code @PreAuthorize}, used only by the demo
 * {@code TestController}.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

	/**
	 * Tells Spring Security how to load a user at login: by email from the {@code users} table.
	 *
	 * @return the {@link UserDetailsServiceImpl}
	 */
	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}

	/** Answers 401 when authentication fails (e.g. wrong password at sign-in). */
	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	/**
	 * The provider that checks a username/password pair: loads the user with
	 * {@link #userDetailsService()} and compares the password with {@link #passwordEncoder()}.
	 *
	 * @return a {@link DaoAuthenticationProvider} ("DAO" = it reads users from storage)
	 */
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}


	/**
	 * Exposes Spring's {@link AuthenticationManager} as a bean so
	 * {@link com.microservices.authendicationservice.services.AuthServiceImpl} can call
	 * {@code authenticate(...)}. It delegates to {@link #authenticationProvider()}.
	 *
	 * @param config Spring's authentication configuration
	 * @return the configured manager
	 * @throws Exception if the manager can't be built
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}


	/**
	 * BCrypt with strength 12 (2^12 hashing rounds, roughly a few hundred ms per hash). Used both
	 * to hash new passwords and to check them at login. The salt is stored inside the hash.
	 *
	 * @return the password encoder
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}

	/**
	 * The HTTP security rules.
	 *
	 * <ul>
	 *   <li>{@code cors()}: uses Spring MVC's CORS settings (e.g. {@code @CrossOrigin}).</li>
	 *   <li>{@code csrf().disable()}: safe because there are no cookies or sessions; clients
	 *       send tokens explicitly.</li>
	 *   <li>Authentication failures go to {@link AuthEntryPointJwt} (401).</li>
	 *   <li>{@code STATELESS}: no HTTP session is created; every request stands alone.</li>
	 *   <li>{@code /auth/**}, {@code /authorization/**} and {@code /test/**} are permitted.</li>
	 *   <li>Password checks use {@link #authenticationProvider()}.</li>
	 * </ul>
	 *
	 * @param http Spring's security builder
	 * @return the filter chain
	 * @throws Exception if the configuration is invalid
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.cors().and().csrf().disable().exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.authorizeHttpRequests().antMatchers("/auth/**","/authorization/**").permitAll().and().authorizeHttpRequests()
				.antMatchers("/test/**").permitAll().and().authenticationProvider(authenticationProvider()).build();

	}
}
