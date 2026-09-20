package com.tekram.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.tekram.response.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UploadOssController {

    private final OSS ossClient;

    // 替换成你创建的Bucket名称
    private static final String BUCKET_NAME = "tials";

    @PostMapping("/files")
    public Result upload(@RequestParam("file") MultipartFile file)
            throws IOException {

        // 1. 判断文件是否为空
        if (file.isEmpty()) {
            return Result.error("请选择图片");
        }

        // 2. 限制文件大小
        if (file.getSize() > 10L * 1024 * 1024) {
            return Result.error("图片不能超过5MiB");
        }

        // 3. 获取文件后缀
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null
                || originalFilename.lastIndexOf(".") < 0) {
            return Result.error("文件格式不正确");
        }

        String extName = originalFilename
                .substring(originalFilename.lastIndexOf("."))
                .toLowerCase(Locale.ROOT);

        String contentType = switch (extName) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".webp" -> "image/webp";
            default -> null;
        };

        if (contentType == null) {
            return Result.error("只支持JPEG、PNG、WebP图片");
        }

        // 4. 生成唯一文件名
        String filename = UUID.randomUUID()
                .toString()
                .replace("-", "") + extName;

        // 在OSS中的对象名称，相当于放在images目录下
        String objectKey = "images/" + filename;

        // 5. 设置文件信息
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(file.getSize());

        // 6. 上传到OSS，上传结束自动关闭输入流
        try (InputStream input = file.getInputStream()) {
            ossClient.putObject(
                    BUCKET_NAME,
                    objectKey,
                    input,
                    metadata
            );
        }

        // 7. 返回文件在OSS中的名称
        return Result.success(objectKey);
    }

    @GetMapping("/files/{filename}/content")
    public ResponseEntity<byte[]> read(
            @PathVariable("filename") String filename
    ) throws IOException {

        // 1. 只允许上传接口生成的文件名
        if (!filename.matches("[a-f0-9]{32}\\.(jpg|jpeg|png|webp)")) {
            return ResponseEntity.notFound().build();
        }

        // 2. 拼接OSS对象名称，必须和上传时保持一致
        String objectKey = "images/" + filename;

        try (OSSObject object =
                     ossClient.getObject(BUCKET_NAME, objectKey)) {

            // 3. 读取图片字节，小图片先用这种简单写法
            byte[] bytes = object.getObjectContent().readAllBytes();

            // 4. 获取上传时设置的图片类型
            String contentType = object.getObjectMetadata()
                    .getContentType();

            // 5. 直接返回图片，不用Result包装
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(bytes);

        } catch (OSSException e) {
            if ("NoSuchKey".equals(e.getErrorCode())) {
                return ResponseEntity.notFound().build();
            }

            // OSS权限或服务异常，不应当作“文件不存在”
            throw e;
        }
    }
}
