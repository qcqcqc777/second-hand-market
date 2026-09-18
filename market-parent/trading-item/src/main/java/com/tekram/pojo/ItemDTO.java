package com.tekram.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ItemDTO {
    private String idleName;
    private String idleDetails;
    private BigDecimal idlePrice;
    private Long categoryId;
    private String idlePlace;
    private List<String> pictureList;
}
