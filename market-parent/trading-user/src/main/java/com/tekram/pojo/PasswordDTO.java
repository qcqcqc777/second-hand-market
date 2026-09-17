package com.tekram.pojo;

import lombok.Data;

@Data
public class PasswordDTO {
    private String newPassword;
    private String oldPassword;
}
