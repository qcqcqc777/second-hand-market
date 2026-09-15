package com.tekram.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单收货地址快照；不关联原地址ID，创建后不可修改，对应数据库 {@code order_address} 表。
 * <p>仅描述持久化字段；默认值、外键和检查约束由 SQL 与业务层负责。
 */
@Data
public class OrderAddress {

    /** 订单地址快照主键。 */
    private Long id;

    /** 所属订单；每个订单必须在业务事务内写入一份。 */
    private Long orderId;

    /** 收货人姓名快照。 */
    private String consigneeName;

    /** 收货电话快照。 */
    private String consigneePhone;

    /** 完整地址快照，创建时组合省市区和详细地址。 */
    private String detailAddress;

    /** 快照生成时间。 */
    private LocalDateTime createTime;
}
