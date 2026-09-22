package com.tekram.pojo;

import lombok.Data;

import java.time.OffsetDateTime;

/** 商品留言响应，对应分页结果 rows 中的一条记录。 */
@Data
public class MessageVO {

    /** message.id；响应使用字符串 ID。 */
    private String id;

    /** message.item_id。 */
    private String itemId;

    /** 通过 message.user_id 查询并组装的作者公开信息。 */
    private AuthorVO author;

    /** message.content；已删除留言应由业务层替换为占位文字。 */
    private String content;

    /** message.parent_id；根留言为 null，回复为根留言的字符串 ID。 */
    private String parentId;

    /** message.is_deleted：0 转为 false，1 转为 true。 */
    private Boolean deleted;

    /** message.create_time；组装时按数据库时间约定补充时区偏移。 */
    private OffsetDateTime createdAt;

    /** 仅包含作者的公开信息。发留言用户 */
    @Data
    public static class AuthorVO {

        /** user.id，对应 message.user_id。 */
        private String id;

        /** user.nickname。 */
        private String nickname;

        /** user.avatar_url。 */
        private String avatarUrl;
    }
}
