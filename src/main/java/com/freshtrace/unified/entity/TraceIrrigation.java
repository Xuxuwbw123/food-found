package com.freshtrace.unified.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data @TableName("trace_irrigation")
public class TraceIrrigation {
    @TableId(type = IdType.ASSIGN_ID) private Long id;
    private Long traceId;
    private LocalDate irrigationDate;
    private String waterSource;
    private BigDecimal waterVolume;
    private String irrigationType;
    private Integer duration;
    private String description;
    private String operator;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
