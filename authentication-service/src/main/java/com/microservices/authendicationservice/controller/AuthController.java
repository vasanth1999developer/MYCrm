package com.microservices.authendicationservice.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.microservices.authendicationservice.response.ApiResponse;
import com.microservices.authendicationservice.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.authendicationservice.jwt.JwtUtils;
import com.microservices.authendicationservice.models.ERole;
import com.microservices.authendicationservice.models.LoginRequestDto;
import com.microservices.authendicationservice.models.Role;
import com.microservices.authendicationservice.models.SignupRequestDto;
import com.microservices.authendicationservice.models.User;
import com.microservices.authendicationservice.repository.RoleRepository;
import com.microservices.authendicationservice.repository.UserRepository;
import com.microservices.authendicationservice.response.JwtResponse;
import com.microservices.authendicationservice.response.MessageResponse;
import com.microservices.authendicationservice.services.UserDetailsImpl;

/**
 * REST endpoints for login users, under the gateway prefix {@code /auth}.
 *
 * <p>Clients reach these through the gateway ({@code /auth/**} route). Which ones need a JWT is
 * decided by the gateway's {@code RouteValidator}: {@code /auth/signin} and
 * {@code /auth/signupcheckEmailId/*} are public, everything else needs a token. This service
 * itself doesn't check JWTs; it only checks {@code X-Gateway-Secret} (see
 * {@link com.microservices.authendicationservice.gatewayauth.GatewayAuthFilter}).
 *
 * <p>All responses use the {@link ApiResponse} envelope.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

	/** Unused here since sign-in moved to {@link AuthService}; kept for the commented-out legacy code. */
	@Autowired
	AuthenticationManager authenticationManager;

	/** Reads and saves {@link User} rows. */
	@Autowired
	UserRepository userRepository;

	/** Unused: roles are stored as plain strings on {@link User}, not as {@link Role} rows. */
	@Autowired
	RoleRepository roleRepository;

	/** BCrypt encoder (strength 12) used to hash new users' passwords. */
	@Autowired
	PasswordEncoder encoder;

	/** Unused here; token creation happens in {@link AuthService}. */
	@Autowired
	JwtUtils jwtUtils;

	/** Performs sign-in: password check, privilege lookup, token creation. */
	@Autowired
	AuthService authService;

//	@PostMapping("/signin")
//	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequest) {
//
//		Authentication authentication = authenticationManager.authenticate(
//				new UsernamePasswordAuthenticationToken(
//						loginRequest.getUsername(),
//						loginRequest.getPassword()
//				)
//		);
//
//		SecurityContextHolder.getContext().setAuthentication(authentication);
//
//		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//
//			if (userDetails.getIsDelete()) {
//				return ResponseEntity
//						.status(HttpStatus.FORBIDDEN)
//						.body( ApiResponse.error(
//
//								"User account is deactivated",
//								null));
//			}
//
//		String jwt = jwtUtils.generateJwtToken(authentication);
//		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
//				.collect(Collectors.toList());
//
//		JwtResponse response = new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles);
//
//		return ResponseEntity
//				.status(HttpStatus.OK)
//				.body( ApiResponse.success(
//
//						"User authenticated successfully",
//						response
//				));
//
//
//
//	}



	/**
	 * {@code POST /auth/signin} (public): logs a user in and returns a JWT.
	 *
	 * <p>Request body: {@code {"username": "<email>", "password": "..."}}. Despite its name,
	 * {@code username} must be the user's <b>email</b>, because that is the login key (see
	 * {@link com.microservices.authendicationservice.services.UserDetailsServiceImpl}).
	 *
	 * <p>Responses:
	 * <ul>
	 *   <li>200: {@code data} is a {@link JwtResponse} with {@code accessToken}, {@code tokenType}
	 *       ("Bearer"), {@code id}, {@code username}, {@code email}, {@code roles}, {@code active}.</li>
	 *   <li>400: a field is blank ({@code @Valid}).</li>
	 *   <li>401: unknown email or wrong password ({@code BadCredentialsException}, turned into 401 by
	 *       Spring Security and {@link com.microservices.authendicationservice.jwt.AuthEntryPointJwt}).</li>
	 *   <li>500: the account is deactivated, or authorization-service is unreachable or returned no
	 *       privileges. There is no exception handler yet to map these to 403/503.</li>
	 * </ul>
	 *
	 * @param loginRequest email and password
	 * @return the token and basic user info, wrapped in {@link ApiResponse}
	 */
	@PostMapping("/signin")
	public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(
			@Valid @RequestBody LoginRequestDto loginRequest) {

		JwtResponse response = authService.signIn(loginRequest);

		return ResponseEntity.ok(
				ApiResponse.success("User authenticated successfully", response));
	}



	/**
	 * {@code POST /auth/users} (JWT required at the gateway): creates a login user.
	 *
	 * <p>Called mainly by employee-service (through Feign, directly via Eureka, with
	 * {@code X-Gateway-Secret}) when an employee is created. The comment says "admin-only", but no
	 * role check is enforced: any authenticated caller can create users.
	 *
	 * <p>The password is hashed with BCrypt before saving. Role names are stored as given; they are
	 * not checked against authorization-service here (employee-service validates them first).
	 *
	 * <p>Responses: 201 with the new user id in {@code data}; 400 if the email already exists, no
	 * role was given, or a field fails validation.
	 *
	 * @param request username (3-20 chars), email, password (6-40 chars) and role names
	 * @return the id of the created user
	 */
	@PostMapping("/users")   // admin-only
	public ResponseEntity<ApiResponse<Long>> createUser(@Valid @RequestBody SignupRequestDto request) {

		if (userRepository.existsByEmail(request.getEmail())) {
			return ResponseEntity.badRequest().body(ApiResponse.error("Email already exists"));
		}

		Set<String> roleNames = request.getRole();
		if (roleNames == null || roleNames.isEmpty()) {
			return ResponseEntity.badRequest().body(ApiResponse.error("At least one role is required"));
		}

		User user = new User(
				request.getUsername(),
				request.getEmail(),
				encoder.encode(request.getPassword()));
		user.setRoles(roleNames);
		user.setDelete(false);

		User saved = userRepository.save(user);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("User created", saved.getId()));
	}




	/**
	 * {@code GET /auth/signupcheckEmailId/{email}} (public): tells whether an email is already registered.
	 *
	 * <p>{@code data} is {@code true} if the email is taken. Note the oddities: it answers
	 * <b>201</b> with the message "User is created", although nothing is created, and the
	 * {@code user} parameter is unused (Spring fills it from query parameters, if any).
	 *
	 * @param email the email to check
	 * @param user  unused
	 * @return {@code true} if a user with this email exists, {@code false} otherwise
	 * @throws Exception declared but never thrown by this code
	 */
	@GetMapping("/signupcheckEmailId/{email}")
	public ResponseEntity<?> checkEmailId(@PathVariable("email") String email, User user) throws Exception {

			boolean status = userRepository.existsByEmail(email);


		return ResponseEntity.status(HttpStatus.CREATED).body( ApiResponse.success(

				"User is created",
				status));
    }


	/**
	 * {@code GET /auth/get-loggedInUserById/{userId}} (JWT required at the gateway): returns one user.
	 *
	 * <p>{@code data} is the {@link User} entity ({@code id}, {@code username}, {@code email},
	 * {@code isDelete}, {@code roles}); the password hash is excluded by {@code @JsonIgnore}.
	 * There's no check that {@code userId} is the caller's own id, so any signed-in user can read
	 * any user.
	 *
	 * @param userId the user's id
	 * @return 200 with the user, or 404 "User not found"
	 */
	@GetMapping("/get-loggedInUserById/{userId}")
	public ResponseEntity<?> getloggedInUserById(@PathVariable long userId) {

		return userRepository.findById(userId)
				.map(user -> ResponseEntity.ok(
						 ApiResponse.success( "User fetched successfully", user)
				))
				.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body( ApiResponse.error( "User not found", null)));
	}
}
