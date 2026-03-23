package com.quazzom.mastermind.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "david",
                        url = "https://linkedin.com/in/phrxn"
                ),
                title = "Mastermind API",
                description = "API for the Mastermind game. This API provides a way for you to play, view your history, edit your profile, and see the Global Ranking!",
                version = "1.0.0"
        )
)
@SecurityScheme(
		name = "bearerAuth",
		description = "The token can be obtained at /api/v1/auth/login. To log in, you must first register at /api/v1/auth/register.",
		type = SecuritySchemeType.HTTP,
		scheme = "bearer",
		bearerFormat="JWT",
		in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
