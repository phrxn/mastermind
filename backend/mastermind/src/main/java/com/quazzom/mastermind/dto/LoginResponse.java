package com.quazzom.mastermind.dto;

public record LoginResponse(
        String token,
        String tokenType
) {
}