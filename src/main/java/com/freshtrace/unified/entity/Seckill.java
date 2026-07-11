package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("seckill")
public class Seckill {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long productId;
    private BigDecimal seckillPrice;
    private Integer stock;
    private Integer sold;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private LocalDateTime createTime;
}
