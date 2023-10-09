package com.springboot.marketplace.controller;

import com.springboot.marketplace.dto.request.LoginRequestDTO;
import com.springboot.marketplace.dto.request.RefreshTokenRequestDTO;
import com.springboot.marketplace.dto.request.RegisterRequestDTO;
import com.springboot.marketplace.dto.response.LoginResponseDTO;
import com.springboot.marketplace.dto.response.MessageResponseDTO;
import com.springboot.marketplace.dto.response.TokenResponseDTO;
import com.springboot.marketplace.model.AppUser;
import com.springboot.marketplace.service.AppUserService;
import com.springboot.marketplace.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final AppUserService appUserService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = authenticationManager.authenticate(usernamePassword);
        AppUser appUser = (AppUser) auth.getPrincipal();
        var token = tokenService.generateToken(appUser.getEmail());
        var refreshToken = tokenService.generateRefreshToken(token);

        return ResponseEntity.ok(new LoginResponseDTO(token, refreshToken));
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponseDTO> register(@RequestBody @Valid RegisterRequestDTO body) {
        appUserService.save(new AppUser(body));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponseDTO("Account created, check your email for verification"));
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmAccount(@RequestParam String verificationToken) {
        appUserService.enableAppUser(verificationToken);

        return ResponseEntity.status(HttpStatus.OK).body("confirmed");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO body) {
        var subject = tokenService.validateRefreshToken(body.refreshToken());
        var token = tokenService.generateToken(subject);

        return ResponseEntity.ok(new TokenResponseDTO(token));
    }
}
