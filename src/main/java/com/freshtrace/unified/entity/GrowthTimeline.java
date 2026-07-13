package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@TableName("growth_timeline")
public class GrowthTimeline {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long traceId;
    private String stage;
    private String title;
    private String content;
    private String imageUrls;
    private java.time.LocalDateTime updateTime;
    private java.time.LocalDateTime createTime;
}
