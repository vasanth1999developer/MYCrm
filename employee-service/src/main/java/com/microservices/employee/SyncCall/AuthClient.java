package com.microservices.employee.SyncCall;

import com.microservices.employee.common.ApiResponse;
import com.microservices.employee.model.CreateUserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "authentication-service")
public interface AuthClient {

    @PostMapping("/auth/users")
    ApiResponse<Long> createUser(@RequestBody CreateUserRequest request);
    // returns the generated user id
}
