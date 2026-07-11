package com.freshtrace.unified.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data @TableName("trace_inspection")
public class TraceInspection {
    @TableId(type = IdType.ASSIGN_ID) private Long id;
    private Long traceId;
    private String inspectionNo;
    private LocalDate inspectionDate;
    private String inspectionAgency;
    private String inspector;
    private Integer inspectionResult;
    private String reportUrl;
    private String conclusion;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
