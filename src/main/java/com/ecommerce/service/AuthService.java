package com.ecommerce.service;

import com.ecommerce.request.SignInRequest;
import com.ecommerce.response.AuthResponse;
import com.ecommerce.response.SignUpRequest;

public interface AuthService {
    void sendLoginOtp(String username) throws Exception;

    String createUser(SignUpRequest signUpRequest) throws Exception;

    AuthResponse signIn(SignInRequest signInRequest);
}
