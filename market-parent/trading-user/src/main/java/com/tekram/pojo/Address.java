package com.tekram.pojo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用户地址簿；可删除，不联动订单快照，对应数据库 {@code address} 表。
 * <p>仅描述持久化字段；默认值、外键和检查约束由 SQL 与业务层负责。
 */
@Data
public class Address {

    /** 地址主键。 */
    private Long id;

    /** 所属用户。 */
    private Long userId;

    /** 收货人姓名。 */
    private String consigneeName;

    /** 收货电话，字符串保留前导零及区号。 */
    private String consigneePhone;

    /** 省。 */
    private String provinceName;

    /** 市。 */
    private String cityName;

    /** 区县。 */
    private String regionName;

    /** 详细地址或校内约定位置。 */
    private String detailAddress;

    /** 0非默认，1默认。 */
    private Byte defaultFlag;

    /** 只读生成列；确保同一用户最多一个默认地址。数据库自动计算，Mapper不得插入或更新此列。 */
    @Setter(AccessLevel.NONE)
    private Long defaultUserId;

    /** 创建时间。 */
    private LocalDateTime createTime;

    /** 更新时间。 */
    private LocalDateTime updateTime;
}
