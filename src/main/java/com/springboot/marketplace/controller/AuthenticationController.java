package com.springboot.marketplace.controller;

import com.springboot.marketplace.dto.request.RegisterRequestDTO;
import com.springboot.marketplace.dto.response.MessageResponseDTO;
import com.springboot.marketplace.model.AppUser;
import com.springboot.marketplace.service.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AppUserService appUserService;

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
}
