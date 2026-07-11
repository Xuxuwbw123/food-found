package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("points_exchange_rule")
public class PointsExchangeRule {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String ruleName;
    private Integer pointsCost;
    private Long couponId;
    private String couponName;
    private BigDecimal couponValue;
    private BigDecimal couponMinAmount;
    private Integer status;
    private LocalDateTime createTime;
}
