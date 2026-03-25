package com.quazzom.mastermind.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.quazzom.mastermind.dto.LoginRequest;
import com.quazzom.mastermind.dto.LoginResponse;
import com.quazzom.mastermind.dto.MeResponse;
import com.quazzom.mastermind.dto.RegisterRequest;
import com.quazzom.mastermind.dto.RegisterResponse;
import com.quazzom.mastermind.exception.UnauthorizedException;
import com.quazzom.mastermind.security.CustomUserDetails;
import com.quazzom.mastermind.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints para se cadastrar, entrar no sistema, e obter informações de login sobre você")
public class AuthController {

    private final AuthService userService;

    public AuthController(AuthService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(
            description = "Registra um novo usuário para utilizar a API.",
            responses = {
                @ApiResponse(
                        responseCode = "400",
                        description = "Se o corpo da requisição estiver vazio, for um JSON mal formatado, ou se alguma propriedade obrigatória estiver faltando no corpo da requisição",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(value = """
									{"status":400, "error":"Corpo da requisição vazio ou em um formato inválido. Ele deve ser um JSON válido contendo os campos esperados."}
								""")
                        )
                ),
                @ApiResponse(
                        responseCode = "422",
                        description = "Se algum campo existir, mas estiver com o valor errado (não respeitar as regras de negócio)",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(value = """
									{"status":422, "error":"A propriedade 'xxxxxxxx' deve ser preenchida, ela não pode estar vazia"}
								""")
                        )
                )
            }
    )
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request,
            UriComponentsBuilder uriComponentsBuilder) {

        RegisterResponse createdUser = userService.register(request);

        return ResponseEntity.status(201).body(createdUser);
    }

    @PostMapping("/login")
    @Operation(
            description = "Autentica um usuário existente com nickname/e-mail e senha. Retorna o token JWT necessário para usar a API",
            responses = {
                @ApiResponse(
                        responseCode = "400",
                        description = "Se o corpo da requisição estiver vazio, for um JSON mal formatado, ou se alguma propriedade obrigatória estiver faltando no corpo da requisição",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(value = """
									{"status":400, "error":"Corpo da requisição vazio ou em um formato inválido. Ele deve ser um JSON válido contendo os campos esperados."}
								""")
                        )
                ),
                @ApiResponse(
                        responseCode = "422",
                        description = "Se algum campo existir, mas estiver com o valor errado (não respeitar as regras de negócio)",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(value = """
									{"status":422, "error":"A propriedade 'xxxxxxxx' deve ser preenchida, ela não pode estar vazia"}
								""")
                        )
                )
            }
    )
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            description = "Checa o token JWT enviado no header Authorization e retorna informações sobre o usuário autenticado.",
            responses = {
                @ApiResponse(
                        responseCode = "401",
                        description = "Se o usuário não estiver autenticado",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(value = """
									{"status":401, "error":"Unauthorized"}
								""")
                        )
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Se o corpo da requisição estiver vazio, for um JSON mal formatado, ou se alguma propriedade obrigatória estiver faltando no corpo da requisição",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(value = """
									{"status":400, "error":"Corpo da requisição vazio ou em um formato inválido. Ele deve ser um JSON válido contendo os campos esperados."}
								""")
                        )
                ),
                @ApiResponse(
                        responseCode = "422",
                        description = "Se algum campo existir, mas estiver com o valor errado (não respeitar as regras de negócio)",
                        content = @Content(
                                mediaType = "application/json",
                                examples = @ExampleObject(value = """
									{"status":422, "error":"A propriedade 'xxxxxxxx' deve ser preenchida, ela não pode estar vazia"}
								""")
                        )
                )
			}
	)
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        UUID userId = extractUserId(authentication);
        return ResponseEntity.ok(userService.me(userId));
    }

    private UUID extractUserId(Authentication authentication) {

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails details) {
            return details.getUuidPublic();
        }

        try {
            return UUID.fromString(authentication.getName());
        } catch (Exception ex) {
            throw new UnauthorizedException("Usuário não autenticado");
        }
    }

}
