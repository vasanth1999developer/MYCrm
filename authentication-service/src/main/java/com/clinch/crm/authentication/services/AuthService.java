package com.clinch.crm.authentication.services;

import com.clinch.crm.authentication.models.LoginRequestDto;
import com.clinch.crm.authentication.response.JwtResponse;

/**
 * Sign-in business logic, kept out of the controller. Implemented by {@link AuthServiceImpl}.
 */
public interface AuthService {

    /**
     * Checks the user's credentials and issues a JWT.
     *
     * @param loginRequest email (in {@code username}) and password
     * @return the signed token plus basic user info
     * @throws org.springframework.security.core.AuthenticationException if the email is unknown or
     *         the password is wrong
     * @throws com.clinch.crm.authentication.common.AccountDeactivatedException if the
     *         account is deactivated
     * @throws com.clinch.crm.authentication.common.AuthorizationUnavailableException if
     *         the user's privileges can't be resolved
     */
    JwtResponse signIn(LoginRequestDto loginRequest);

}
