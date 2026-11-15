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

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;


    private final AuthorizationClient authorizationClient;

    private final JwtUtils jwtUtils;

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