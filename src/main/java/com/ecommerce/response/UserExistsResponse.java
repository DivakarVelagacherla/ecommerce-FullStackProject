package com.ecommerce.response;

import lombok.Data;

@Data
public class UserExistsResponse {
    private String username;
    private String message;
}
