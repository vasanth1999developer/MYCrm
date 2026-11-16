package com.microservices.authendicationservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Demo endpoints from the original Spring Security tutorial, showing {@code @PreAuthorize}.
 *
 * <p>Unreachable in practice: {@code /api/test} has no gateway route, and direct calls are
 * rejected by the gateway-secret filter. The roles checked here (USER, MODERATOR, ADMIN, BUYER)
 * aren't MyCrm roles. Listed for deletion in {@code docs/KNOWN_ISSUES.md}.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
	/** @return a fixed string; no role required */
	@GetMapping("/all")
	public String allAccess() {
		return "Public Content.";
	}

	/** @return a fixed string; requires USER, MODERATOR or ADMIN */
	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public String userAccess() {
		return "User Content.";
	}

	/** @return a fixed string; requires MODERATOR */
	@GetMapping("/mod")
	@PreAuthorize("hasRole('MODERATOR')")
	public String moderatorAccess() {
		return "Moderator Board.";
	}

	/** @return a fixed string; requires ADMIN */
	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "Admin Board.";
	}


	/** @return a fixed string; requires BUYER */
	@GetMapping("/buyer")
	@PreAuthorize("hasRole('BUYER')")
	public String userAcess() {
		return "User board";
	}
}
