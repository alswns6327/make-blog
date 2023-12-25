package org.mj.blog.service;

import lombok.RequiredArgsConstructor;
import org.mj.blog.config.jwt.TokenProvider;
import org.mj.blog.domain.RefreshToken;
import org.mj.blog.domain.User;
import org.mj.blog.dto.TokenDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public TokenDto createNewAccessToken(String refreshToken){
        // 토큰 유효성 검사에 실패하면 예외 발생
        if(!tokenProvider.validToken(refreshToken)){
            throw new IllegalArgumentException("Unexpected token");
        }

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(userId);

        return tokenProvider.generateToken(user);
    }

    public TokenDto login(User user, BCryptPasswordEncoder bCryptPasswordEncoder) {
        User findUser = userService.findByEmail(user.getEmail());


//        if(!bCryptPasswordEncoder.encode(user.getPassword()).equals(findUser.getPassword())){
//            throw new IllegalArgumentException("password error " + user.getEmail());
//        };

        TokenDto tokenDto = tokenProvider.generateToken(findUser);

        RefreshToken refreshToken = new RefreshToken(findUser.getId(), tokenDto.getRefreshToken());
        refreshTokenService.save(refreshToken);
        System.out.println("---------------");
        System.out.println(tokenDto.getAccessToken());
        System.out.println(tokenDto.getRefreshToken());
        return tokenDto;
    }
}
