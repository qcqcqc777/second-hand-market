package com.tekram.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 一件闲置商品，不含库存或SKU，对应数据库 {@code item} 表。
 * <p>仅描述持久化字段；默认值、外键和检查约束由 SQL 与业务层负责。
 */
@Data
public class Item {

    /** 商品主键。 */
    private Long id;

    /** 卖家用户ID，创建后不可转让。 */
    private Long userId;

    /** 分类ID。 */
    private Long categoryId;

    /** 商品标题。 */
    private String idleName;

    /** 商品详情，纯文本。 */
    private String idleDetails;

    /** 有序图片路径数组，第一张为封面；允许空数组。保存JSON文本，接口DTO/VO按需转换为结构化对象。 */
    private String pictureList;

    /** 当前售价，人民币元，必须大于0。 */
    private BigDecimal idlePrice;

    /** 交易地点描述；本期单校园，不宣称校园认证。 */
    private String idlePlace;

    /** 成色：0未标注，1全新，2几乎全新，3正常使用，4明显使用痕迹。 */
    private Byte conditionLevel;

    /** 销售状态：1在售，2卖家下架，3待付款占用，4已成交锁定。 */
    private Byte itemStatus;

    /** 管理状态：0正常，1屏蔽；独立于销售状态。 */
    private Byte moderationStatus;

    /** 当前屏蔽原因，历史见审计。 */
    private String moderationReason;

    /** 0未删除，1卖家逻辑删除；历史交易仍可引用。 */
    private Byte isDeleted;

    /** 版本号，影响购买资格的更新必须递增。 */
    private Integer version;

    /** 首次发布时间。 */
    private LocalDateTime createTime;

    /** 更新时间。 */
    private LocalDateTime updateTime;
}
