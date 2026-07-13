package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@TableName("farm_update")
public class FarmUpdate {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long farmerId;
    private String updateType;
    private String title;
    private String content;
    private String imageUrls;
    private String videoUrl;
    private java.time.LocalDateTime createTime;
}
