package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@TableName("charity_cert")
public class CharityCert {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private String certNo;
    private String title;
    private String content;
    private String imageUrl;
    private java.math.BigDecimal totalAmount;
    private java.time.LocalDateTime createTime;
}
