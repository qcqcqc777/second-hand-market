package com.tekram.pojo;

public record FileUploadVO(String id,
                           String url,
                           String contentType,
                           long size) {
}
