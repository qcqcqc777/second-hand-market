package com.tekram.service;

import com.tekram.pojo.LoginDTO;
import com.tekram.pojo.LoginVO;
import com.tekram.pojo.RegisterDTO;
import com.tekram.pojo.User;

public interface AuthService {
    /*
    用户注册
     */
    void register(RegisterDTO registerDTO);
    /*
    用户登录
     */
    LoginVO login(LoginDTO loginDTO);
}
