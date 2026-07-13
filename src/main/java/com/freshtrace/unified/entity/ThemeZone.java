package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@TableName("theme_zone")
public class ThemeZone {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String name;
    private String description;
    private String bannerImage;
    private String bgColor;
    private String season;
    private String festival;
    private Integer status;
    private java.time.LocalDateTime startTime;
    private java.time.LocalDateTime endTime;
    private java.time.LocalDateTime createTime;
}
