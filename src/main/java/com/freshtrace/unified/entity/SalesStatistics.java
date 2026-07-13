package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@TableName("sales_statistics")
public class SalesStatistics {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long productId;
    private Long farmerId;
    private java.time.LocalDate statDate;
    private Integer quantity;
    private java.math.BigDecimal amount;
    private java.time.LocalDateTime createTime;
}
