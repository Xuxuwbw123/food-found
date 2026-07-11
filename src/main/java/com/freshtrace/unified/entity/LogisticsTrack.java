package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("logistics_track")
public class LogisticsTrack {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long logisticsId;
    private Long orderId;
    private String station;
    private String location;
    private String description;
    private LocalDateTime trackTime;
    private LocalDateTime createTime;
}
