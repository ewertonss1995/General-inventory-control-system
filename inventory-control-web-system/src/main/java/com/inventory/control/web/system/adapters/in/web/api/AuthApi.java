package com.inventory.control.web.system.adapters.in.web.api;

import com.inventory.control.web.system.adapters.in.web.dto.request.LoginRequest;
import com.inventory.control.web.system.adapters.in.web.dto.request.RegisterUserRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.ErrorResponse;
import com.inventory.control.web.system.adapters.in.web.dto.response.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Autenticação Web/BFF", description = "Endpoints de facilitação para registro de usuários e autenticação no ecossistema Web")
public interface AuthApi {

    @Operation(
        summary = "Registra um novo usuário via BFF Web",
        description = "Orquestra a criação de um novo usuário junto ao serviço de autenticação central. Valida os campos antes de encaminhar a requisição."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuário registrado com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Erro de validação no corpo da requisição ou dados malformados",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "ErroDeValidacao",
                    summary = "Exemplo de erro de validação de dados",
                    value = """
                        {
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Erro na validação dos campos informados.",
                          "path": "/api/v1/auth/register",
                          "timestamp": "2026-10-07T11:40:00Z",
                          "fieldErrors": [
                            {
                              "field": "username",
                              "message": "Username é obrigatório"
                            }
                          ]
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflito informado pelo serviço upstream (usuário/e-mail já cadastrado)",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erro de integração com o Auth-service ou falha interna do BFF",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    void registerUser(@Valid @RequestBody RegisterUserRequest request);

    @Operation(
        summary = "Autentica o usuário e obtém o Token JWT",
        description = "Envia as credenciais do usuário ao Auth-service downstream e retorna os tokens e tempos de expiração."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Autenticação bem-sucedida",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = TokenResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Credenciais inválidas ou erro nos parâmetros de entrada",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "CredenciaisInvalidas",
                    summary = "Exemplo de erro de login",
                    value = """
                        {
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Usuário ou senha inválidos",
                          "path": "/api/v1/auth/login",
                          "timestamp": "2026-10-07T11:40:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "502",
            description = "Falha de comunicação ou integração com o serviço de autenticação downstream",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request);
}