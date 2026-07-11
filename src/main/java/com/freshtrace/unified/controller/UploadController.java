package com.freshtrace.unified.controller;

import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.entity.TraceImage;
import com.freshtrace.unified.service.TraceImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired private TraceImageService traceImageService;

    @Value("${upload.path:./uploads}")
    private String uploadPath;

    @PostMapping("/trace-image")
    public Result<?> uploadTraceImage(@RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Long traceId,
            @RequestParam(required = false, defaultValue = "general") String imageType) throws IOException {
        if (file.isEmpty()) return Result.error(400, "缺少文件");
        String ext = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString() + ext;
        String dir = uploadPath + "/trace/";
        new File(dir).mkdirs();
        file.transferTo(new File(dir + filename));
        String imageUrl = "/uploads/trace/" + filename;
        if (traceId != null) {
            TraceImage img = new TraceImage();
            img.setTraceId(traceId); img.setImageType(imageType); img.setImageUrl(imageUrl); img.setSort(0);
            traceImageService.save(img);
        }
        return Result.success("上传成功", new HashMap<String,String>() {{ put("imageUrl", imageUrl); }});
    }

    @PostMapping("/product-image")
    public Result<?> uploadProductImage(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) return Result.error(400, "缺少文件");
        String ext = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString() + ext;
        String dir = uploadPath + "/products/";
        new File(dir).mkdirs();
        file.transferTo(new File(dir + filename));
        String imageUrl = "/uploads/products/" + filename;
        return Result.success("上传成功", new HashMap<String,String>() {{ put("imageUrl", imageUrl); }});
    }

    private String getExtension(String filename) {
        if (filename == null) return ".png";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : ".png";
    }
}
