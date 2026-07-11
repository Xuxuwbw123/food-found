package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("product_image")
public class ProductImage {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long productId;
    private String imageUrl;
    private Integer imageType;
    private Integer sort;
    private LocalDateTime createTime;
}
