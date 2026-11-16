package com.microservices.authorizationservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Empty placeholder mapped to {@code /authorization}; it has no endpoints. The real endpoints are in
 * {@link RoleController}, {@link PrivilegeController} and {@link AccessController}.
 */
@RestController
@RequestMapping("authorization")
public class AuthorizationController {

}
