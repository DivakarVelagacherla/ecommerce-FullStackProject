package com.ecommerce.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
public class APIResponse {

    private String message;

    public void setMessage(String message){
        this.message = message;
    }
;}