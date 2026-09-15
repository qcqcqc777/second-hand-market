package com.tekram.service.impl;

import com.tekram.mapper.UserMapper;
import com.tekram.pojo.LoginDTO;
import com.tekram.pojo.LoginVO;
import com.tekram.pojo.RegisterDTO;
import com.tekram.pojo.User;
import com.tekram.service.AuthService;
import com.tekram.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void register(RegisterDTO dto) {
        User user = new User();
        user.setAccountNumber(dto.getAccountNumber());
        user.setNickname(dto.getNickname());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        userMapper.addUser(user);
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        User userLogin = userMapper.getLoginInfoByAccountAndPassward(dto);
        if (userLogin == null || !passwordEncoder.matches(dto.getPassword(), userLogin.getPasswordHash())) {
            // 返回“账号或密码错误”
            return null;
        }
        if (userLogin != null && Byte.valueOf((byte) 0).equals(userLogin.getUserStatus())) {
            //生成jwt令牌
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", userLogin.getId());
            map.put("accountNumber", userLogin.getAccountNumber());
            String jwt = JwtUtils.generateJwt(map);
            LoginVO loginInfo = new LoginVO(userLogin.getId(), userLogin.getAccountNumber(), userLogin.getNickname(), userLogin.getAvatarUrl(), jwt);
            return loginInfo;
        }
        return null;

    }
}
