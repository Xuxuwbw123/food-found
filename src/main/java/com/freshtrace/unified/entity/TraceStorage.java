package com.freshtrace.unified.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data @TableName("trace_storage")
public class TraceStorage {
    @TableId(type = IdType.ASSIGN_ID) private Long id;
    private Long traceId;
    private String warehouseName;
    private String warehouseAddress;
    private LocalDateTime inTime;
    private LocalDateTime outTime;
    private Integer storageStatus;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
