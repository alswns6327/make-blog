package org.mj.blog.controller.config.jwt;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mj.blog.config.jwt.JwtProperties;
import org.mj.blog.config.jwt.TokenProvider;
import org.mj.blog.domain.User;
import org.mj.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
public class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProperties jwtProperties;

    // generateToken() 검증 테스트
    @DisplayName("generateToken()")
    @Test
    void generateToken(){
        //given
        User testUser = userRepository.save(User.builder().email("alswnsdl@gmail.com").password("test").build());

        //when
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        //then
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getId());
    }

    //validToken() 검즘
    @DisplayName("validToken() 만료된 토큰 검증")
    @Test
    void validToken_invalidToken(){
        // given
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);

        // when
        boolean result = tokenProvider.validToken(token);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("validToken() 유효한 토큰 검증")
    @Test
    void validToken_validToken(){
        // given
        String token = JwtFactory.withDefaultValues()
                .createToken(jwtProperties);

        // when
        boolean result = tokenProvider.validToken(token);

        // then
        assertThat(result).isTrue();
    }

    // getAuthentication() 검증
    @DisplayName("getAuthentication() : 토큰 기반으로 인증 정보가져 오기")
    @Test
    void getAuthentication(){
        // given
        String userEmail = "alswnsdl333@gmail.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);

        // when
        Authentication authentication = tokenProvider.getAuthentication(token);

        // then
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
    }

    @DisplayName("getUserId() 검증")
    @Test
    void getUserId(){
        // given
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);

        // when
        Long userIdByToken = tokenProvider.getUserId(token);

        // then
        assertThat(userIdByToken).isEqualTo(userId);
    }
}
