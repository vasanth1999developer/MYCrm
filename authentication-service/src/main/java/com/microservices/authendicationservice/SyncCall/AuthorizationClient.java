package com.microservices.authendicationservice.SyncCall;

import com.microservices.authendicationservice.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;


@FeignClient(name = "authorization-service")
public interface AuthorizationClient {

    @PostMapping("/authorization/privilege/privileges")
    ApiResponse<Set<String>> getPrivilegesForRoles(@RequestBody Set<String> roleNames);
}