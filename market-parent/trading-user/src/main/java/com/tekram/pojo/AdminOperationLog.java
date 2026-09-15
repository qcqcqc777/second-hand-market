package com.tekram.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理审计；记录成功管理操作与敏感订单访问，只追加，对应数据库 {@code admin_operation_log} 表。
 * <p>仅描述持久化字段；默认值、外键和检查约束由 SQL 与业务层负责。
 */
@Data
public class AdminOperationLog {

    /** 审计主键。 */
    private Long id;

    /** 操作管理员。 */
    private Long adminId;

    /** 操作码，如USER_BAN、ITEM_BLOCK、ORDER_VIEW。 */
    private String action;

    /** 目标类型：USER、ITEM、ORDER、MESSAGE、CATEGORY。 */
    private String targetType;

    /** 目标主键；多态逻辑引用，由服务端校验。 */
    private Long targetId;

    /** 操作理由，封禁屏蔽删除必填。 */
    private String reason;

    /** 变更前的最小必要字段；禁止保存密码令牌和完整联系方式。保存JSON文本，接口DTO/VO按需转换为结构化对象。 */
    private String beforeData;

    /** 变更后的最小必要字段；查询操作可为空。保存JSON文本，接口DTO/VO按需转换为结构化对象。 */
    private String afterData;

    /** 请求追踪标识，不作为唯一键。 */
    private String requestId;

    /** 操作时间。 */
    private LocalDateTime createTime;
}
