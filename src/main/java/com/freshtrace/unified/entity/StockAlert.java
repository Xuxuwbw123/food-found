package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@TableName("stock_alert")
public class StockAlert {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long productId;
    private Integer alertQuantity;
    private java.time.LocalDateTime lastNotified;
    private java.time.LocalDateTime createTime;
}
