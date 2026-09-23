package com.tekram.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** 创建订单及订单详情接口的响应数据；持久化字段按数据库列名的驼峰形式命名，由统一 Result 包装。 */
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class TradeOrderVO {

    /** 订单主键，以字符串返回。 */
    private String id;

    /** 业务订单号。 */
    private String orderNumber;

    /** 买家用户 ID。 */
    private String buyerId;

    /** 卖家用户 ID。 */
    private String sellerId;

    /** 下单时的商品快照。 */
    private ItemSnapshotVO item;

    /** 应付金额，以字符串返回，例如 "88.00"。 */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal orderPrice;

    /** 对外展示的订单状态，例如 WAIT_PAY；由业务层转换实体中的状态码。 */
    private String orderStatus;

    /** 下单时的收货地址快照。 */
    private AddressSnapshotVO address;

    /** 创建时间，包含时区偏移。 */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime createTime;

    /** 付款截止时间。 */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime expireTime;

    /** 支付时间，未支付为 null。 */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime paymentTime;

    /** 交付时间，未交付为 null。 */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime deliveryTime;

    /** 完成时间，未完成为 null。 */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime completeTime;

    /** 取消时间，未取消为 null。 */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime cancelTime;

    /** 卖家交付说明。 */
    private String deliveryNote = "";

    /** 取消原因。 */
    private String cancelReason = "";

    /** 商品快照，仅包含订单展示所需的字段。 */
    @Data
    public static class ItemSnapshotVO {
        private String itemId;
        private String itemNameSnapshot;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private BigDecimal itemPriceSnapshot;

        /** 封面图片地址，返回普通 URL 字符串。 */
        private String itemCoverSnapshot;
    }

    /** 收货地址快照。 */
    @Data
    public static class AddressSnapshotVO {
        private String consigneeName;
        private String consigneePhone;
        private String detailAddress;
    }
}
