package org.mj.blog.controller;

import lombok.RequiredArgsConstructor;
import org.mj.blog.domain.User;
import org.mj.blog.dto.CreateAccessTokenRequest;
import org.mj.blog.dto.CreateAccessTokenResponse;
import org.mj.blog.dto.TokenDto;
import org.mj.blog.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenApiController {
    private final TokenService tokenService;

    @PostMapping("/api/login")
    public ResponseEntity<TokenDto> login(@RequestBody User user, BCryptPasswordEncoder bCryptPasswordEncoder) {
        return ResponseEntity.ok(tokenService.login(user, bCryptPasswordEncoder));
    }
}
