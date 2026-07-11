package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("trace_scan_log")
public class TraceScanLog {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private LocalDateTime createTime;
}
