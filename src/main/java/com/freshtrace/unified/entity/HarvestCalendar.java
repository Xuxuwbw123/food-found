package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@TableName("harvest_calendar")
public class HarvestCalendar {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long farmerId;
    private String productName;
    private Long categoryId;
    private String season;
    private Integer harvestMonth;
    private String description;
    private String imageUrl;
    private Integer status;
    private java.time.LocalDateTime createTime;
}
