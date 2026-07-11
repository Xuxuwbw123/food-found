package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("member_level_config")
public class MemberLevelConfig {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Integer level;
    private String levelName;
    private BigDecimal discountRate;
    private BigDecimal upgradeAmount;
    private BigDecimal rechargeMin;
    private Integer pointsRate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
