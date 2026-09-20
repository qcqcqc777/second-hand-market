//package com.tekram.controller;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import com.tekram.response.Result;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Locale;
//import java.util.UUID;
//
//@Slf4j
//@RestController
//@RequestMapping
//public class UploadController {
//    // 图片保存目录
//    private static final String UPLOAD_DIR = "D:/images/";
//
//    @PostMapping("/files")
//    public Result upload(@RequestParam("file") MultipartFile file)
//            throws IOException {
//
//        // 1. 判断文件是否为空
//        if (file.isEmpty()) {
//            return Result.error("请选择图片");
//        }
//
//        // 2. 限制大小为5MiB
//        if (file.getSize() > 10L * 1024 * 1024) {
//            return Result.error("图片不能超过10MiB");
//        }
//
//        // 3. 获取原文件名和后缀
//        String originalFilename = file.getOriginalFilename();
//
//        if (originalFilename == null
//                || originalFilename.lastIndexOf(".") < 0) {
//            return Result.error("文件格式不正确");
//        }
//
//        String extName = originalFilename
//                .substring(originalFilename.lastIndexOf("."))
//                .toLowerCase(Locale.ROOT);
//
//        if (!extName.equals(".jpg")
//                && !extName.equals(".jpeg")
//                && !extName.equals(".png")
//                && !extName.equals(".webp")) {
//            return Result.error("只支持JPEG、PNG、WebP图片");
//        }
//
//        // 4. 生成唯一文件名，避免重名覆盖
//        String filename = UUID.randomUUID()
//                .toString()
//                .replace("-", "") + extName;
//
//        // 5. 如果目录不存在，就创建
//        File directory = new File(UPLOAD_DIR);
//
//        if (!directory.isDirectory()
//                && !directory.mkdirs()
//                && !directory.isDirectory()) {
//            throw new IOException("上传目录创建失败");
//        }
//
//        // 6. 保存文件
//        File targetFile = new File(directory, filename);
//        file.transferTo(targetFile);
//
//        // 7. 返回保存后的文件名
//        return Result.success(filename);
//    }
//
//}
