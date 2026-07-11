package com.freshtrace.unified.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data @TableName("trace_harvest")
public class TraceHarvest {
    @TableId(type = IdType.ASSIGN_ID) private Long id;
    private Long traceId;
    private LocalDateTime harvestDate;
    private BigDecimal harvestQuantity;
    private String harvestUnit;
    private String harvestMethod;
    private String description;
    private String operator;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
