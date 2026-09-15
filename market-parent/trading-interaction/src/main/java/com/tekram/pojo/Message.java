package com.tekram.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品留言和两层回复；不是即时聊天，对应数据库 {@code message} 表。
 * <p>仅描述持久化字段；默认值、外键和检查约束由 SQL 与业务层负责。
 */
@Data
public class Message {

    /** 留言主键。 */
    private Long id;

    /** 所属商品。 */
    private Long itemId;

    /** 作者用户ID。 */
    private Long userId;

    /** 接收用户；根留言接收者为卖家，回复接收者为根留言作者。 */
    private Long toUserId;

    /** 根留言ID；NULL表示根留言；本期最多两层。 */
    private Long parentId;

    /** 纯文本内容；删除后接口只显示占位。 */
    private String content;

    /** 0正常，1删除；保留回复关联。 */
    private Byte isDeleted;

    /** 删除原因，管理员删除必填。 */
    private String deleteReason;

    /** 删除时间。 */
    private LocalDateTime deleteTime;

    /** 留言时间。 */
    private LocalDateTime createTime;
}
