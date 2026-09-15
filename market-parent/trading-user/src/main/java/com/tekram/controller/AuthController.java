package com.tekram.controller;

import com.tekram.pojo.LoginDTO;
import com.tekram.pojo.LoginVO;
import com.tekram.pojo.RegisterDTO;
import com.tekram.pojo.User;
import com.tekram.response.Result;
import com.tekram.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public Result register(@RequestBody RegisterDTO dto) {
        authService.register(dto);
        return Result.success();
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO  loginDTO) {
        LoginVO loginInfo = authService.login(loginDTO);
        if (loginInfo == null) {
            return Result.error("账号或密码错误，或账号不可用");
        }
        return Result.success(loginInfo);
    }
}
