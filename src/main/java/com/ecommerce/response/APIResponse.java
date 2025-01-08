package com.ecommerce.response;

import lombok.Data;

@Data
public class APIResponse {

    private String message;

    public void setMessage(String message){
        this.message = message;
    }
;}