package com.tekram.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单状态历史，与状态变更同事务；只追加，对应数据库 {@code order_status_log} 表。
 * <p>仅描述持久化字段；默认值、外键和检查约束由 SQL 与业务层负责。
 */
@Data
public class OrderStatusLog {

    /** 状态记录主键。 */
    private Long id;

    /** 所属订单。 */
    private Long orderId;

    /** 转换前状态；首次创建为NULL。 */
    private Byte fromStatus;

    /** 转换后状态，与订单状态编码一致。 */
    private Byte toStatus;

    /** 转换后的订单版本，首次为0。 */
    private Integer orderVersion;

    /** 1用户，2系统；管理员本期不能修改订单状态。 */
    private Byte operatorType;

    /** 用户操作时必填；系统操作为NULL。 */
    private Long operatorUserId;

    /** 操作说明，禁止记录密码或令牌。 */
    private String reason;

    /** 操作时间。 */
    private LocalDateTime createTime;
}
