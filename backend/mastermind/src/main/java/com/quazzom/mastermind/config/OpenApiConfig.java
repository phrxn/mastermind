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
                description = "API para o jogo Mastermind. Esta API permite que você jogue, veja seu histórico, edite seu perfil e confira o Ranking Global!",
                version = "1.0.0"
        )
)
@SecurityScheme(
		name = "bearerAuth",
		description = "O token pode ser obtido em /api/v1/auth/login. Para fazer login, você deve primeiro se registrar em /api/v1/auth/register.",
		type = SecuritySchemeType.HTTP,
		scheme = "bearer",
		bearerFormat="JWT",
		in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
