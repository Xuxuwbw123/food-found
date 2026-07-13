package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@TableName("trace_location")
public class TraceLocation {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long traceId;
    private String locationType;
    private String locationName;
    private java.math.BigDecimal latitude;
    private java.math.BigDecimal longitude;
    private String description;
    private Integer sort;
    private java.time.LocalDateTime createTime;
}
