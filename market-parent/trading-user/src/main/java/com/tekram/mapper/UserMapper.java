package com.tekram.mapper;

import com.tekram.pojo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    int insertUser(User user);
    User selectUserByAccountNumber(LoginDTO loginDTO);
    UserVO selectUserById(@Param("id") Long id);
    int updateUserProfileById(User  user);
    int updatePasswordHashById(@Param("id") Long id,@Param("passwordHash") String passwordHash);
    String selectPasswordHashById(@Param("id") Long id);

}
