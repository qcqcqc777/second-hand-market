package com.tekram.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginVO {
    private Long id;
    private String accountNumber;
    private String nickname;
    private String avatarUrl;
    private String token;
}
