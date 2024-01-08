package org.mj.blog.service;

import lombok.RequiredArgsConstructor;
import org.mj.blog.config.jwt.TokenProvider;
import org.mj.blog.domain.RefreshToken;
import org.mj.blog.domain.User;
import org.mj.blog.dto.AddUserRequest;
import org.mj.blog.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;

    public Long save(AddUserRequest dto){
        System.out.println("-------------");
        System.out.println(dto.getEmail());
        System.out.println(dto.getPassword());
        System.out.println("-------------");
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .build()).getId();
    }

    public User findById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    @Transactional
    public RefreshToken login(String email, String password) {
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
        String refreshToken = tokenProvider.generateToken(user, Duration.ofHours(2));
        return new RefreshToken(user.getId(), refreshToken);
    }
}
