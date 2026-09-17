package com.tekram.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tekram.mapper.UserMapper;
import com.tekram.pojo.*;
import com.tekram.response.PageResult;
import com.tekram.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserVO getUserProfile(Long id) {
        UserVO user = userMapper.selectUserById(id);
        return user;
    }

    @Transactional
    @Override
    public UserVO updateUserProfile(User user) {
        userMapper.updateUserProfileById(user);
        return userMapper.selectUserById(user.getId());
    }

    @Transactional
    @Override
    public boolean changePassword(Long id, PasswordDTO dto) {
        if (dto == null || dto.getNewPassword() == null
                || dto.getOldPassword() == null || dto.getNewPassword().isBlank()) {
            return false;
        }
        String oldPassword = dto.getOldPassword();
        String newPassword = dto.getNewPassword();
        if (oldPassword.equals(newPassword)) {
            return false;
        }
        String passwordHash = userMapper.selectPasswordHashById(id);
        if (passwordHash == null || !passwordEncoder.matches(oldPassword, passwordHash)) {
            return false;
        }
        String newPasswordHash = passwordEncoder.encode(newPassword);
        return userMapper.updatePasswordHashById(id, newPasswordHash) == 1;

    }


}