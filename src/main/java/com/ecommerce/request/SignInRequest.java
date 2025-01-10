package com.ecommerce.request;

import lombok.Data;

@Data
public class SignInRequest {
    private String email;
    private String otp;

}
