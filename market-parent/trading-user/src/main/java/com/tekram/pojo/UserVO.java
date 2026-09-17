package com.tekram.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    private Long id;
    private String accountNumber;
    private String nickname;
    private String avatarUrl;
    private Byte userStatus;
    private LocalDateTime createTime;
}
