package com.tekram.pojo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 一件商品一笔订单；不支持付款后取消退款；历史不删除，对应数据库 {@code trade_order} 表。
 * <p>仅描述持久化字段；默认值、外键和检查约束由 SQL 与业务层负责。
 */
@Data
public class TradeOrder {

    /** 订单主键。 */
    private Long id;

    /** 服务端生成的业务订单号。 */
    private String orderNumber;

    /** 买家用户ID。 */
    private Long buyerId;

    /** 卖家用户ID，下单时从商品读取并冻结。 */
    private Long sellerId;

    /** 商品ID；取消后可再次生成新订单。 */
    private Long itemId;

    /** 下单时商品标题快照。 */
    private String itemNameSnapshot;

    /** 下单时封面快照；无图为NULL。 */
    private String itemCoverSnapshot;

    /** 下单时商品价格快照。 */
    private BigDecimal itemPriceSnapshot;

    /** 应付金额；本期无运费优惠，等于商品快照价。 */
    private BigDecimal orderPrice;

    /** 0待付款，1待交付，2待收货，3完成，4取消。 */
    private Byte orderStatus;

    /** 只读生成列；一个商品最多一笔非取消订单，已完成仍占用。数据库自动计算，Mapper不得插入或更新此列。 */
    @Setter(AccessLevel.NONE)
    private Long exclusiveItemId;

    /** 创建订单幂等键，同一买家同一键只创建一次。 */
    private String requestKey;

    /** 规范化下单请求SHA256；同键不同参数拒绝。 */
    private String requestHash;

    /** 付款截止时间，服务端设为创建时间加30分钟。 */
    private LocalDateTime expireTime;

    /** 支付确认时间。 */
    private LocalDateTime paymentTime;

    /** 卖家交付说明；不表示接入物流系统。 */
    private String deliveryNote;

    /** 卖家确认交付时间。 */
    private LocalDateTime deliveryTime;

    /** 买家确认收货时间。 */
    private LocalDateTime completeTime;

    /** 取消类型：1买家取消，2系统超时。 */
    private Byte cancelType;

    /** 取消原因。 */
    private String cancelReason;

    /** 取消时间。 */
    private LocalDateTime cancelTime;

    /** 订单状态版本，每次成功转换递增。 */
    private Integer version;

    /** 下单时间。 */
    private LocalDateTime createTime;

    /** 更新时间。 */
    private LocalDateTime updateTime;
}
