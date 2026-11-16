package com.microservices.employee.SyncCall;

import com.microservices.employee.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client for authorization-service, used to check a role before creating an employee.
 *
 * <p>Calls go straight to an instance found through Eureka; {@code GatewaySecretFeignInterceptor}
 * adds {@code X-Gateway-Secret}.
 */
@FeignClient(name = "authorization-service")   // the Eureka service name
public interface AuthorizationClient {

    /**
     * Calls {@code GET /authorization/role/exists?roleName=...}.
     *
     * @param roleName the exact role name
     * @return the envelope; {@code data} is {@code true} if a role with this name exists
     *         (including inactive or deleted roles)
     * @throws feign.FeignException if the call fails
     */
    @GetMapping("/authorization/role/exists")
    ApiResponse<Boolean> roleExists(@RequestParam("roleName") String roleName);

}
