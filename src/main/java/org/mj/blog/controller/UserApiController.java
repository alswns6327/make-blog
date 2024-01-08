package org.mj.blog.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.mj.blog.domain.RefreshToken;
import org.mj.blog.domain.User;
import org.mj.blog.dto.AddUserRequest;
import org.mj.blog.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


@RequiredArgsConstructor
@Controller
public class UserApiController {

    private final UserService userService;

    @PostMapping("/user")
    public String singup(@RequestBody AddUserRequest request) {
        userService.save(request); // 회원 가입 메서드 호출
        return "redirect:/login"; // 회원가입이 완료된 이후 로그인 페이지 이동
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        new SecurityContextLogoutHandler().logout(request, response,
                SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }

    @ResponseBody
    @PostMapping("/login")
    public ResponseEntity<RefreshToken> login(@RequestBody User user) {
        String email = user.getUsername();
        String password = user.getPassword();
        RefreshToken refreshToken = userService.login(email, password);
        return ResponseEntity.ok().body(refreshToken);
    }
}
