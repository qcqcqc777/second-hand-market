package com.tekram.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 单层商品分类字典，对应数据库 {@code category} 表。
 * <p>仅描述持久化字段；默认值、外键和检查约束由 SQL 与业务层负责。
 */
@Data
public class Category {

    /** 分类主键，保留旧分类1至4。 */
    private Long id;

    /** 分类名称。 */
    private String categoryName;

    /** 排序，越小越靠前。 */
    private Integer sortOrder;

    /** 1可发布，0停止新发布；历史商品保留关联。 */
    private Byte enabled;

    /** 创建时间。 */
    private LocalDateTime createTime;

    /** 更新时间。 */
    private LocalDateTime updateTime;
}
