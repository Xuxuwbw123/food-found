package com.freshtrace.unified.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
@Data @TableName("trace_processing")
public class TraceProcessing {
    @TableId(type = IdType.ASSIGN_ID) private Long id;
    private Long traceId;
    private LocalDateTime processingDate;
    private String processingType;
    private String processingMethod;
    private String description;
    private String operator;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
