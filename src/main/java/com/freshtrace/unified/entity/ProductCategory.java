package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("product_category")
public class ProductCategory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long parentId;
    private String categoryName;
    private String categoryIcon;
    private String categoryImage;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
