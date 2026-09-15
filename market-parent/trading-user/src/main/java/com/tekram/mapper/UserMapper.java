package com.tekram.mapper;

import com.tekram.pojo.LoginDTO;
import com.tekram.pojo.RegisterDTO;
import com.tekram.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    int addUser(User user);
    User getLoginInfoByAccountAndPassward(LoginDTO loginDTO);
}
