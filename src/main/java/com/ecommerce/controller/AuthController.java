package com.ecommerce.controller;

import com.ecommerce.config.JWT_CONSTANT;
import com.ecommerce.enums.USER_ROLE;
import com.ecommerce.model.VerificationCode;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.request.SignInRequest;
import com.ecommerce.response.APIResponse;
import com.ecommerce.response.AuthResponse;
import com.ecommerce.response.SignUpRequest;
import com.ecommerce.response.UserExistsResponse;
import com.ecommerce.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody SignUpRequest signUpRequest) throws Exception {


        String createUserResponse = authService.createUser(signUpRequest);
        if (createUserResponse.startsWith(JWT_CONSTANT.JWT_KEY)) {
            String jwt = createUserResponse.substring(JWT_CONSTANT.JWT_KEY.length());
            AuthResponse authResponse = new AuthResponse();
            authResponse.setJwt(jwt);
            authResponse.setMessage("Registered Successfully");
            authResponse.setRole(USER_ROLE.USER_CUSTOMER);
            return ResponseEntity.ok(authResponse);
        } else {

            UserExistsResponse userExistsResponse = new UserExistsResponse();
            userExistsResponse.setUsername(signUpRequest.getEmail());
            userExistsResponse.setMessage("User already Exists");
            return ResponseEntity.ok(userExistsResponse);
        }


    }

    @PostMapping("/send/login-signup-otp")
    public ResponseEntity<APIResponse> sendOtp(@RequestBody VerificationCode otpRequest) throws Exception {

        System.out.println("In Send OTP method");
        System.out.println(otpRequest);

        authService.sendLoginOtp(otpRequest.getEmail());

        APIResponse apiResponse = new APIResponse();

        apiResponse.setMessage("OTP sent successfully");
        return ResponseEntity.ok(apiResponse);

    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody SignInRequest signInRequest) throws Exception {

        AuthResponse authResponse = authService.signIn(signInRequest);
        return ResponseEntity.ok(authResponse);

    }
}
