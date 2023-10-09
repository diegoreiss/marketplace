package com.springboot.marketplace.controller;

import com.springboot.marketplace.dto.request.LoginRequestDTO;
import com.springboot.marketplace.dto.request.RefreshTokenRequestDTO;
import com.springboot.marketplace.dto.request.RegisterRequestDTO;
import com.springboot.marketplace.dto.response.LoginResponseDTO;
import com.springboot.marketplace.dto.response.MessageResponseDTO;
import com.springboot.marketplace.dto.response.StandardErrorResponseDTO;
import com.springboot.marketplace.dto.response.TokenResponseDTO;
import com.springboot.marketplace.model.AppUser;
import com.springboot.marketplace.service.AppUserService;
import com.springboot.marketplace.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@Tag(name = "auth", description = "Recursos para autenticação de usuários")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/auth", produces = {"application/json"})
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final AppUserService appUserService;
    private final TokenService tokenService;

    @Operation(summary = "Realiza o login do usuário", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Autenticação realizada com sucesso", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "412", description = "Dados rejeitados", content = @Content(schema = @Schema(implementation = StandardErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar a autenticação do usuário", content = @Content(schema = @Schema(implementation = StandardErrorResponseDTO.class)))
    })
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = authenticationManager.authenticate(usernamePassword);
        AppUser appUser = (AppUser) auth.getPrincipal();
        var token = tokenService.generateToken(appUser.getEmail());
        var refreshToken = tokenService.generateRefreshToken(token);

        return ResponseEntity.ok(new LoginResponseDTO(token, refreshToken));
    }

    @Operation(summary = "Realiza o cadastro de um usuário", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro de usuário realizado com sucesso", content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Usuário já cadastrado", content = @Content(schema = @Schema(implementation = StandardErrorResponseDTO.class))),
            @ApiResponse(responseCode = "412", description = "Dados rejeitados", content = @Content(schema = @Schema(implementation = StandardErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar o registro do usuário", content = @Content(schema = @Schema(implementation = StandardErrorResponseDTO.class)))
    })
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponseDTO> register(@RequestBody @Valid RegisterRequestDTO body) {
        appUserService.save(new AppUser(body));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponseDTO("Account created, check your email for verification"));
    }

    @Operation(summary = "Realiza a operação de ativação do usuário", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário ativado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar a ativação do usuário", content = @Content(schema = @Schema(implementation = StandardErrorResponseDTO.class)))
    })
    @PostMapping(value = "/confirm", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> confirmAccount(@RequestParam String verificationToken) {
        appUserService.enableAppUser(verificationToken);

        return ResponseEntity.status(HttpStatus.OK).body("confirmed");
    }

    @Operation(summary = "Realiza a operação de refresh token jwt", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token atualizado com sucesso", content = @Content(schema = @Schema(implementation = TokenResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro ao realizar a atualização do token", content = @Content(schema = @Schema(implementation = StandardErrorResponseDTO.class)))
    })
    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO body) {
        var subject = tokenService.validateRefreshToken(body.refreshToken());
        var token = tokenService.generateToken(subject);

        return ResponseEntity.ok(new TokenResponseDTO(token));
    }
}
