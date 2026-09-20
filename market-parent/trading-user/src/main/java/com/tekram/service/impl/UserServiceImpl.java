package com.tekram.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tekram.mapper.UserMapper;
import com.tekram.pojo.*;
import com.tekram.response.PageResult;
import com.tekram.service.UserService;
import com.tekram.util.ImageReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
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
    @Autowired
    private ImageReference imageReference;

    @Override
    public UserVO getUserProfile(Long id) {
        UserVO user = userMapper.selectUserById(id);
        if (user != null) {
            user.setAvatarUrl(imageReference.toUrl(user.getAvatarUrl()));
        }
        return user;
    }

    @Transactional
    @Override
    public UserVO updateUserProfile(UserProfileDTO dto, Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        User user = new User();
        user.setId(userId);
        user.setNickname(dto.getNickname());
        if (dto.getAvatarKey() != null) {
            // 沿用现有 avatar_url 列保存 objectKey，响应时再转成 URL。
            user.setAvatarUrl(imageReference.toKey(dto.getAvatarKey()));
        }
        if ((user.getNickname() == null || user.getNickname().isBlank())
                && user.getAvatarUrl() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请提供昵称或头像");
        }
        userMapper.updateUserProfileById(user);
        return getUserProfile(userId);
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
