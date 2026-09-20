package com.tekram.service;
import com.tekram.pojo.Address;
import com.tekram.pojo.PasswordDTO;
import com.tekram.pojo.User;
import com.tekram.pojo.UserProfileDTO;
import com.tekram.pojo.UserVO;
import com.tekram.response.PageResult;

import java.util.List;

public interface UserService {
    /*
    当前用户资料
     */
    UserVO getUserProfile(Long id);
    /*
    修改用户信息
     */
    UserVO updateUserProfile(UserProfileDTO dto, Long userId);
    /*
    修改密码
     */
    boolean changePassword(Long id, PasswordDTO dto);

}
