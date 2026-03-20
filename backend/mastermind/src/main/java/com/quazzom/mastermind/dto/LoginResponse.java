package com.quazzom.mastermind.dto;

public class LoginResponse {

    private final String token;
    private final String tokenType;

    public LoginResponse(String token) {
        this.token = token;
        this.tokenType = "Bearer";
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }
}