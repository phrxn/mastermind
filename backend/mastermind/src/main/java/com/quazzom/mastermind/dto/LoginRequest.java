package com.quazzom.mastermind.dto;

public record LoginRequest(
        String username,
        String password
) {
}