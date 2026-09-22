package com.tekram.pojo;

import lombok.Data;

@Data
public class MessageDTO {
    private String content;
    private Long parentId;
}
