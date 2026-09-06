package com.microservices.authendicationservice.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.microservices.authendicationservice.response.ApiResponse;
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

@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginRequest.getUsername(),
						loginRequest.getPassword()
				)
		);


		SecurityContextHolder.getContext().setAuthentication(authentication);
		

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();


			if (userDetails.getIsDelete()) {
				return ResponseEntity
						.status(HttpStatus.FORBIDDEN)
						.body( ApiResponse.error(

								"User account is deactivated",
								null));
			}

		String jwt = jwtUtils.generateJwtToken(authentication);
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		JwtResponse response = new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body( ApiResponse.success(

						"User authenticated successfully",
						response
				));



	}

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




	@GetMapping("/signupcheckEmailId/{email}")
	public ResponseEntity<?> checkEmailId(@PathVariable("email") String email, User user) throws Exception {

			boolean status = userRepository.existsByEmail(email);


		return ResponseEntity.status(HttpStatus.CREATED).body( ApiResponse.success(

				"User is created",
				status));
    }


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
