package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@TableName("green_points_rule")
public class GreenPointsRule {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String ruleName;
    private String actionType;
    private Integer points;
    private Integer status;
    private java.time.LocalDateTime createTime;
}
