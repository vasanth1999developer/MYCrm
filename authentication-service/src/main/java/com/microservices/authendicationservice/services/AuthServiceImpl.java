package com.microservices.authendicationservice.services;

import com.microservices.authendicationservice.SyncCall.AuthorizationClient;
import com.microservices.authendicationservice.common.AccountDeactivatedException;
import com.microservices.authendicationservice.common.AuthorizationUnavailableException;
import com.microservices.authendicationservice.jwt.JwtUtils;
import com.microservices.authendicationservice.models.LoginRequestDto;
import com.microservices.authendicationservice.response.JwtResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default {@link AuthService}: the sign-in flow.
 *
 * <ol>
 *   <li>Spring Security checks email and password ({@link AuthenticationManager} →
 *       {@link UserDetailsServiceImpl} loads the user → BCrypt compares the password).</li>
 *   <li>Deactivated accounts ({@code isDelete = true}) are rejected.</li>
 *   <li>The user's privileges are fetched from authorization-service for their roles.</li>
 *   <li>A JWT with id, email, username, roles and privileges is signed by {@link JwtUtils}.</li>
 * </ol>
 *
 * <p>Dependencies are injected through the Lombok-generated constructor
 * ({@code @RequiredArgsConstructor} on the {@code final} fields).
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    /** Spring Security's entry point for checking credentials. */
    private final AuthenticationManager authenticationManager;


    /** Feign client for authorization-service, used to resolve privileges. */
    private final AuthorizationClient authorizationClient;

    /** Signs the JWT. */
    private final JwtUtils jwtUtils;

    /**
     * {@inheritDoc}
     *
     * <p>The deactivation check runs <b>after</b> the password check, so a deactivated user with a
     * wrong password gets 401 like anyone else, and only the correct password reveals that the
     * account is deactivated.
     */
    @Override
    public JwtResponse signIn(LoginRequestDto loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()));

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if (userDetails.getIsDelete()) {
            throw new AccountDeactivatedException("User account is deactivated");
        }

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Set<String> privileges = fetchPrivileges(new HashSet<>(roles), userDetails.getEmail());

        String jwt = jwtUtils.generateToken(
                userDetails.getId(),
                userDetails.getEmail(),
                userDetails.getUsername(),
                new HashSet<>(roles),
                privileges);

        return new JwtResponse(jwt, userDetails.getId(),
                userDetails.getUsername(), userDetails.getEmail(), roles);
    }

    /**
     * Asks authorization-service which privileges the given roles grant.
     *
     * <p>Fails closed: if the call fails for any reason, or returns no privileges, sign-in fails
     * rather than issuing a token without privileges.
     *
     * @param roles role names of the user, e.g. {@code [SALES_REP]}
     * @param email the user's email, only for log messages
     * @return the union of privileges for those roles, never empty
     * @throws AuthorizationUnavailableException if authorization-service is unreachable, errors,
     *         or returns an empty/null set
     */
    private Set<String> fetchPrivileges(Set<String> roles, String email) {
        try {
            Set<String> privileges =
                    authorizationClient.getPrivilegesForRoles(roles).getData();

            if (privileges == null || privileges.isEmpty()) {
                log.error("No privileges resolved for {} with roles {}", email, roles);
                throw new AuthorizationUnavailableException(
                        "No privileges resolved for roles " + roles, null);
            }
            return privileges;

        } catch (AuthorizationUnavailableException e) {
            throw e;                                  // already ours — let it through
        } catch (Exception e) {
            log.error("Could not reach authorization-service during login for {}", email, e);
            throw new AuthorizationUnavailableException(
                    "Permissions service is unavailable", e);
        }
    }
}
