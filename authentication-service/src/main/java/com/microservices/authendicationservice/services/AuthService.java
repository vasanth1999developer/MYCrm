package com.microservices.authendicationservice.services;

import com.microservices.authendicationservice.models.LoginRequestDto;
import com.microservices.authendicationservice.response.JwtResponse;

public interface AuthService {

    JwtResponse signIn(LoginRequestDto loginRequest);

}
