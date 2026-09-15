package com.tekram.pojo;

import lombok.Data;
@Data
public class RegisterDTO {
    /** 账号 */
    private String accountNumber;

    /** 密码。 */

    private String password;

    /** 昵称。 */
    private String nickname;
}
