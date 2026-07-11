package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("marketing_activity")
public class MarketingActivity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String name;
    private String type;
    private String rule;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private LocalDateTime createTime;
}
