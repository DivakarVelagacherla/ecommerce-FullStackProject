package com.ecommerce.controller;

import com.ecommerce.response.APIResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping
    public APIResponse homeScreen() {
        APIResponse apiResponse = new APIResponse();
        apiResponse.setMessage("Welcome");
        return apiResponse;
    }
}
