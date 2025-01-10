package com.ecommerce.service;

import com.ecommerce.model.User;

public interface UserService {
    User findUserByJwtToken(String jwt) throws Exception;

    User findUserByEmail(String email) throws Exception;
}
