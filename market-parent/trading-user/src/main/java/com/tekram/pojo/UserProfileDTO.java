package com.tekram.pojo;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class UserProfileDTO {
    private String nickname;

    /** POST /files 返回的 images/文件名；兼容原 avatarUrl 请求字段。 */
    @JsonAlias("avatarUrl")
    private String avatarKey;
}
