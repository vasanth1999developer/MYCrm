package com.microservices.authorizationservice.SyncCall;

import com.microservices.authorizationservice.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

@FeignClient(name = "authorization-service")
public interface AuthorizationClient {

    @PostMapping("/authorization/role/privileges")
    ApiResponse<Set<String>> getPrivilegesForRoles(@RequestBody Set<String> roleNames);
}