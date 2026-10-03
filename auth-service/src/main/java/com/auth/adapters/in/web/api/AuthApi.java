package com.auth.adapters.in.web.api;

import com.auth.adapters.in.web.dto.ErrorResponse;
import com.auth.adapters.in.web.dto.LoginRequest;
import com.auth.adapters.in.web.dto.RegisterRequest;
import com.auth.adapters.in.web.dto.TokenResponse;
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

@Tag(name = "Autenticação", description = "Endpoints para registro de usuários e autenticação/geração de tokens JWT")
public interface AuthApi {

    @Operation(
        summary = "Registra um novo usuário no sistema",
        description = "Cria um novo usuário a partir das informações fornecidas (username, email e senha). Não aceita e-mails ou usernames duplicados."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuário cadastrado com sucesso"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Erro de validação nos campos informados ou violação de regra de negócio",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "ErroDeValidacao",
                    summary = "Exemplo de erro de validação de campos",
                    value = """
                        {
                          "status": 400,
                          "error": "Erro de Validação nos Dados Enviados",
                          "message": "Um ou mais campos contêm valores inválidos. Verifique os detalhes.",
                          "path": "/v1/auth/register",
                          "timestamp": "2026-10-03T19:00:00Z",
                          "fieldErrors": [
                            {
                              "field": "email",
                              "message": "Formato de e-mail inválido"
                            }
                          ]
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflito de dados (usuário ou e-mail já cadastrado)",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "Conflito",
                    summary = "Exemplo de registro duplicado",
                    value = """
                        {
                          "status": 409,
                          "error": "Conflito de Dados",
                          "message": "Já existe um registro com os dados informados ou o formato é incompatível com as regras de integridade.",
                          "path": "/v1/auth/register",
                          "timestamp": "2026-10-03T19:00:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno do servidor ou erro no banco de dados",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request);

    @Operation(
        summary = "Autentica um usuário e gera o token JWT",
        description = "Valida as credenciais (username ou e-mail com senha) e retorna o token de acesso Bearer JWT."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Autenticação realizada com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = TokenResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Requisição malformada, campos obrigatórios ausentes ou credenciais inválidas",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "CredenciaisInvalidas",
                    summary = "Exemplo de falha de autenticação",
                    value = """
                        {
                          "status": 400,
                          "error": "Regra de Negócio Violada",
                          "message": "Usuário ou senha inválidos",
                          "path": "/v1/auth/login",
                          "timestamp": "2026-10-03T19:00:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erro na geração do token JWT ou erro interno de infraestrutura",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request);
}