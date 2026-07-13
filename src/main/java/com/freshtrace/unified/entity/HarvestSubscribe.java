package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@TableName("harvest_subscribe")
public class HarvestSubscribe {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private Long calendarId;
    private Integer notified;
    private java.time.LocalDateTime createTime;
}
