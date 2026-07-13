package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@TableName("mystery_box")
public class MysteryBox {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String name;
    private String description;
    private java.math.BigDecimal price;
    private String imageUrl;
    private String categoryHint;
    private String avoidHint;
    private Integer status;
    private java.time.LocalDateTime createTime;
}
