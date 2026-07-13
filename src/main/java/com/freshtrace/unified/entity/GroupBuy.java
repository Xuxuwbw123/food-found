package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@TableName("group_buy")
public class GroupBuy {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long productId;
    private java.math.BigDecimal groupPrice;
    private java.math.BigDecimal originalPrice;
    private Integer groupSize;
    private Integer currentCount;
    private Integer status;
    private java.time.LocalDateTime startTime;
    private java.time.LocalDateTime endTime;
    private java.time.LocalDateTime createTime;
}
