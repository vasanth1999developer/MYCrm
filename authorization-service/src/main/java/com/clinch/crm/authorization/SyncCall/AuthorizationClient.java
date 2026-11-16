package com.clinch.crm.authorization.SyncCall;

import com.clinch.crm.authorization.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

/**
 * Dead code: a Feign client that points at this same service, at a path that doesn't exist
 * ({@code /authorization/role/privileges}; the real one is {@code /authorization/privilege/privileges}).
 *
 * <p>Feign isn't enabled in this service (no {@code @EnableFeignClients}), so no implementation is
 * ever created. It looks like a copy of authentication-service's client. Listed for deletion in
 * {@code docs/KNOWN_ISSUES.md}.
 */
@FeignClient(name = "authorization-service")
public interface AuthorizationClient {

    /**
     * Never called.
     *
     * @param roleNames role names
     * @return privilege names, if the endpoint existed
     */
    @PostMapping("/authorization/role/privileges")
    ApiResponse<Set<String>> getPrivilegesForRoles(@RequestBody Set<String> roleNames);
}
