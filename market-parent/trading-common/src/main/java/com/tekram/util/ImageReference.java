package com.tekram.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.regex.Pattern;

/** 数据库存储 OSS objectKey，响应时转换为图片读取接口 URL。 */
@Component
public class ImageReference {
    private static final Pattern KEY =
            Pattern.compile("images/[a-f0-9]{32}\\.(jpg|jpeg|png|webp)");

    private final String baseUrl;

    public ImageReference(@Value("${file.public-base-url:http://localhost:${server.port:8082}}")
                          String baseUrl) {
        this.baseUrl = baseUrl.replaceAll("/+$", "");
    }

    public String toKey(String value) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "图片标识不能为空");
        }
        // 允许编辑页面把本服务返回的读取 URL 原样提交回来。
        String path = value.startsWith(baseUrl + "/files/")
                ? value.substring(baseUrl.length()) : value;
        if (path.startsWith("/files/") && path.endsWith("/content")) {
            path = "images/" + path.substring(7, path.length() - 8);
        }
        if (!KEY.matcher(path).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "图片标识格式不正确");
        }
        return path;
    }

    public List<String> toKeys(List<String> values) {
        return values == null ? List.of() : values.stream().map(this::toKey).toList();
    }

    public String toUrl(String value) {
        if (value != null && KEY.matcher(value).matches()) {
            return baseUrl + "/files/" + value.substring(7) + "/content";
        }
        // 兼容数据库里原先保存的头像和商品图片 URL。
        return value;
    }
}
