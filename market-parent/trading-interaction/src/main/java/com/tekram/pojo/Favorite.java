package com.tekram.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏关系；取消收藏物理删除，重复收藏受唯一约束保护，对应数据库 {@code favorite} 表。
 * <p>仅描述持久化字段；默认值、外键和检查约束由 SQL 与业务层负责。
 */
@Data
public class Favorite {

    /** 收藏主键。 */
    private Long id;

    /** 收藏用户。 */
    private Long userId;

    /** 收藏商品。 */
    private Long itemId;

    /** 收藏时间。 */
    private LocalDateTime createTime;
}
