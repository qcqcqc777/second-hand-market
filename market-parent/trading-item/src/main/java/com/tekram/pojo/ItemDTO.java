package com.tekram.pojo;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonAlias;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ItemDTO {
    private String idleName;
    private String idleDetails;
    private BigDecimal idlePrice;
    private Long categoryId;
    private String idlePlace;
    /** 接收上传得到的 objectKey 列表，兼容原 pictureList 字段。 */
    @JsonAlias("imageKeys")
    private List<String> pictureList;
}
