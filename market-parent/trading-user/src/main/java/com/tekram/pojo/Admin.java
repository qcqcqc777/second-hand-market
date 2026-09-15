package com.tekram.pojo;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 管理员；离线初始化，不提供默认明文账号，对应数据库 {@code admin} 表。
 * <p>仅描述持久化字段；默认值、外键和检查约束由 SQL 与业务层负责。
 * <p>包含密码哈希，不得直接作为接口响应；对外返回专用 VO。
 */
@Data
public class Admin {

    /** 管理员主键。 */
    private Long id;

    /** 管理员账号，与普通用户是不同身份空间。 */
    private String accountNumber;

    /** 管理员密码哈希。 */
    @ToString.Exclude
    private String passwordHash;

    /** 管理员显示名称。 */
    private String adminName;

    /** 0启用，1停用；本期只有单一管理角色。 */
    private Byte adminStatus;

    /** 最后成功登录时间。 */
    private LocalDateTime lastLoginTime;

    /** 创建时间。 */
    private LocalDateTime createTime;

    /** 更新时间。 */
    private LocalDateTime updateTime;
}
