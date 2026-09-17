package com.tekram.controller;

import com.tekram.pojo.Address;
import com.tekram.pojo.PasswordDTO;
import com.tekram.pojo.User;
import com.tekram.pojo.UserVO;
import com.tekram.response.PageResult;
import com.tekram.response.Result;
import com.tekram.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/me")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public Result getMe(@RequestAttribute("currentUserId") Long userId) {
        log.info("getMe:userId={}", userId);
        UserVO vo = userService.getUserProfile(userId);
        return Result.success(vo);
    }

    @PutMapping
    public Result updateMe(@RequestBody User user, @RequestAttribute("currentUserId") Long userId) {
        log.info("updateMe:userId={}", userId);
        user.setId(userId);
        UserVO vo = userService.updateUserProfile(user);
        return Result.success(vo);
    }

    @PutMapping("/password")
    public Result changePassword(@RequestBody PasswordDTO dto, @RequestAttribute("currentUserId") Long userId) {
        log.info("changePassword:userId={}", userId);
        boolean isSuccess = userService.changePassword(userId, dto);
        if (isSuccess) {
            return Result.success();
        } else return Result.error("输入错误");
    }


}
