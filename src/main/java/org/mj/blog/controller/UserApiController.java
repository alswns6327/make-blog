package org.mj.blog.controller;

import lombok.RequiredArgsConstructor;
import org.mj.blog.dto.AddUserRequest;
import org.mj.blog.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;


@RequiredArgsConstructor
@Controller
public class UserApiController {

    private final UserService userService;

    @PostMapping("/user")
    public String singup(AddUserRequest request) {
        userService.save(request); // 회원 가입 메서드 호출
        return "redirect:/login"; // 회원가입이 완료된 이후 로그인 페이지 이동
    }
}
