package com.ecommerce.response;

import com.ecommerce.enums.USER_ROLE;
import lombok.Data;

@Data
public class AuthResponse {
    private String jwt;
    private USER_ROLE role;
    private String message;
}
