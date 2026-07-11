package com.freshtrace.unified.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data @TableName("trace_pesticide")
public class TracePesticide {
    @TableId(type = IdType.ASSIGN_ID) private Long id;
    private Long traceId;
    private LocalDate useDate;
    private String pesticideName;
    private BigDecimal dosage;
    private Integer safetyInterval;
    private String description;
    private String operator;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
