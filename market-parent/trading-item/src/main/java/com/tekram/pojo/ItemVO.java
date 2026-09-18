package com.tekram.pojo;

import lombok.Data;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

/** 商品接口响应；与数据库实体分离，图片直接返回保存的路径。 */
@Data
public class ItemVO {
    private Long id;
    private String idleName;
    private String idleDetails;
    private BigDecimal idlePrice;
    private Long categoryId;
    private String idlePlace;
    // 数据库 JSON 文本在响应中转换为图片路径数组。
    private List<String> pictureList;
    private SellerVO seller;
    private Byte itemStatus;
    private Byte moderationStatus;
    private Integer version;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 仅包含卖家的公开信息。 */
    @Data
    public static class SellerVO {
        private Long id;
        private String nickname;
        private String avatarUrl;
    }
}
