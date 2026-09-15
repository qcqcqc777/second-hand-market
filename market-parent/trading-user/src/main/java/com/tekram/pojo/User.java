package com.tekram.pojo;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 用户；不物理删除被交易引用的账号，对应数据库 {@code user} 表。
 * <p>仅描述持久化字段；默认值、外键和检查约束由 SQL 与业务层负责。
 * <p>包含密码哈希，不得直接作为接口响应；对外返回专用 VO。
 */
@Data
public class User {

    /** 用户主键。 */
    private Long id;

    /** 登录账号，当前为手机号；服务端规范化后保存。 */
    private String accountNumber;

    /** 密码哈希，禁止保存或返回明文密码。 */
    @ToString.Exclude
    private String passwordHash;

    /** 昵称。 */
    private String nickname;

    /** 头像相对资源路径或受控URL；空值使用默认头像。 */
    private String avatarUrl;

    /** 账号状态：0正常，1封禁。 */
    private Byte userStatus;

    /** 最近一次封禁原因，解封时清空；历史见管理审计。 */
    private String banReason;

    /** 最后成功登录时间。 */
    private LocalDateTime lastLoginTime;

    /** 注册时间。 */
    private LocalDateTime createTime;

    /** 更新时间。 */
    private LocalDateTime updateTime;
}
