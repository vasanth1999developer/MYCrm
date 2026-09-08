package com.microservices.employee.SyncCall;

import com.microservices.employee.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "authorization-service")   // the Eureka service name
public interface AuthorizationClient {

    @GetMapping("/authorization/role/exists")
    ApiResponse<Boolean> roleExists(@RequestParam("roleName") String roleName);

}
