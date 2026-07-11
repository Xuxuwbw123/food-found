package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("point_log")
public class PointLog {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private String type;
    private Integer point;
    private Integer balance;
    private String remark;
    private Long relationId;
    private LocalDateTime createTime;
}
