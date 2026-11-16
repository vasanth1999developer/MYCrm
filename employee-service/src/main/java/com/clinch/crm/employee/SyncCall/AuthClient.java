package com.clinch.crm.employee.SyncCall;

import com.clinch.crm.employee.common.ApiResponse;
import com.clinch.crm.employee.model.CreateUserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for authentication-service, used to create the login user of a new employee.
 *
 * <p>Calls go straight to an authentication-service instance found through Eureka (not through
 * the gateway, so no JWT is checked); {@code GatewaySecretFeignInterceptor} adds
 * {@code X-Gateway-Secret}.
 */
@FeignClient(name = "authentication-service")
public interface AuthClient {

    /**
     * Calls {@code POST /auth/users}.
     *
     * @param request username, email, password and role names
     * @return the envelope; {@code data} is the new user's id
     * @throws feign.FeignException if the callee answers with an error, e.g. 400 when the email
     *         already exists or a field fails its validation
     */
    @PostMapping("/auth/users")
    ApiResponse<Long> createUser(@RequestBody CreateUserRequest request);
    // returns the generated user id
}
