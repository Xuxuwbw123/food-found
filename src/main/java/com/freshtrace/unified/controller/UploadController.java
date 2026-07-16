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
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired private TraceImageService traceImageService;

    @Value("${upload.path:${user.dir}/uploads}")
    private String uploadPath;

    private static final Set<String> ALLOWED_EXT = new HashSet<>(Arrays.asList(".jpg",".jpeg",".png",".gif",".webp",".bmp"));
    private static final Set<String> ALLOWED_MIME = new HashSet<>(Arrays.asList("image/jpeg","image/png","image/gif","image/webp","image/bmp"));

    private static boolean isValidImageMagic(byte[] header) {
        if (header == null || header.length < 4) return false;
        int b0 = header[0]&0xFF, b1 = header[1]&0xFF, b2 = header[2]&0xFF, b3 = header[3]&0xFF;
        if (b0==0xFF && b1==0xD8 && b2==0xFF) return true;  // JPEG
        if (b0==0x89 && b1==0x50 && b2==0x4E && b3==0x47) return true;  // PNG
        if (b0==0x47 && b1==0x49 && b2==0x46 && b3==0x38) return true;  // GIF
        if (b0==0x52 && b1==0x49 && b2==0x46 && b3==0x46) return true;  // WEBP
        if (b0==0x42 && b1==0x4D) return true;  // BMP
        return false;
    }

    private String validateImageUpload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return "缂哄皯鏂囦欢";
        if (file.getSize() > 5*1024*1024) return "鏂囦欢瓒呰繃5MB";
        String ext = getExtension(file.getOriginalFilename()).toLowerCase();
        if (!ALLOWED_EXT.contains(ext)) return "鍙敮鎸佸浘鐗囨牸寮忥細jpg/jpeg/png/gif/webp/bmp";
        String ct = file.getContentType();
        if (ct == null || !ALLOWED_MIME.contains(ct.toLowerCase())) return "MIME类型不合法";
        byte[] header = new byte[8];
        try (InputStream is = file.getInputStream()) { if (is.read(header) < 4) return "文件为空"; }
        if (!isValidImageMagic(header)) return "文件内容非图片";
        return null;
    }

    @PostMapping("/trace-image")
    public Result<?> uploadTraceImage(@RequestParam("file") MultipartFile file,
            @RequestParam(required=false) Long traceId,
            @RequestParam(required=false,defaultValue="general") String imageType) throws IOException {
        String err = validateImageUpload(file);
        if (err != null) return Result.error(400, err);
        String ext = getExtension(file.getOriginalFilename()).toLowerCase();
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
        return Result.success("涓婁紶鎴愬姛", new HashMap<String,String>() {{ put("imageUrl", imageUrl); }});
    }

    @PostMapping("/product-image")
    public Result<?> uploadProductImage(@RequestParam("file") MultipartFile file) throws IOException {
        String err = validateImageUpload(file);
        if (err != null) return Result.error(400, err);
        String ext = getExtension(file.getOriginalFilename()).toLowerCase();
        String filename = UUID.randomUUID().toString() + ext;
        String dir = uploadPath + "/products/";
        new File(dir).mkdirs();
        file.transferTo(new File(dir + filename));
        return Result.success("涓婁紶鎴愬姛", new HashMap<String,String>() {{ put("imageUrl", "/uploads/products/" + filename); }});
    }

    private String getExtension(String fn) {
        if (fn == null || fn.trim().isEmpty() || fn.contains("..") || fn.contains("/") || fn.contains("\\\\")) return ".png";
        int dot = fn.lastIndexOf('.');
        if (dot < 0) return ".png";
        String ext = fn.substring(dot).toLowerCase();
        if (!ALLOWED_EXT.contains(ext)) return ".png";
    }
}