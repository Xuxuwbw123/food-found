package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("theme_goods")
public class ThemeGoods {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long themeId;
    private Long productId;
    private Integer sort;
}
