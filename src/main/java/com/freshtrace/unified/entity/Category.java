package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("product_category")
public class Category {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long parentId;
    private String categoryName;
    private String categoryIcon;
    private String categoryImage;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
