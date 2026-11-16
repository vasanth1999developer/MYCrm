package com.microservices.authendicationservice.SyncCall;

import com.microservices.authendicationservice.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;


/**
 * Feign client for authorization-service, used at sign-in to resolve privileges.
 *
 * <p>Feign generates the implementation at startup. {@code name} is the Eureka name, so calls go
 * directly to a live authorization-service instance (not through the gateway).
 * {@link com.microservices.authendicationservice.gatewayauth.GatewaySecretFeignInterceptor} adds
 * {@code X-Gateway-Secret} to each call.
 */
@FeignClient(name = "authorization-service")
public interface AuthorizationClient {

    /**
     * Calls {@code POST /authorization/privilege/privileges} on authorization-service.
     *
     * @param roleNames role names, sent as a JSON array, e.g. {@code ["SALES_REP"]}
     * @return the callee's {@link ApiResponse} envelope; {@code data} is the set of privilege names
     *         granted by those roles
     * @throws feign.FeignException if the call fails or returns an error status
     */
    @PostMapping("/authorization/privilege/privileges")
    ApiResponse<Set<String>> getPrivilegesForRoles(@RequestBody Set<String> roleNames);
}
