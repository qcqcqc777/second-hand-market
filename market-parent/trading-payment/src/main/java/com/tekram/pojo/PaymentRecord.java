package com.tekram.pojo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务端模拟支付记录；不等于真实资金结算账本，对应数据库 {@code payment_record} 表。
 * <p>仅描述持久化字段；默认值、外键和检查约束由 SQL 与业务层负责。
 */
@Data
public class PaymentRecord {

    /** 支付尝试主键。 */
    private Long id;

    /** 服务端生成的支付单号。 */
    private String paymentNumber;

    /** 业务订单ID；一个订单可以有多次已关闭或失败的尝试。 */
    private Long orderId;

    /** 本期仅MOCK；真实渠道须另行扩展状态与补偿设计。 */
    private String paymentChannel;

    /** 支付金额，服务端从订单读取，人民币元。 */
    private BigDecimal paymentAmount;

    /** 0待确认，1成功，2关闭，3失败。 */
    private Byte paymentStatus;

    /** 只读生成列；同一订单最多一个待确认或成功的支付尝试。数据库自动计算，Mapper不得插入或更新此列。 */
    @Setter(AccessLevel.NONE)
    private Long exclusiveOrderId;

    /** 同一订单创建支付的幂等键。 */
    private String requestKey;

    /** 规范化支付请求SHA256。 */
    private String requestHash;

    /** 模拟支付成功流水号；由服务端生成。 */
    private String channelTradeNumber;

    /** 失败或关闭原因。 */
    private String failureReason;

    /** 支付尝试截止时间，不晚于订单截止时间。 */
    private LocalDateTime expireTime;

    /** 成功时间。 */
    private LocalDateTime successTime;

    /** 关闭或失败时间。 */
    private LocalDateTime closeTime;

    /** 创建时间。 */
    private LocalDateTime createTime;

    /** 更新时间。 */
    private LocalDateTime updateTime;
}
