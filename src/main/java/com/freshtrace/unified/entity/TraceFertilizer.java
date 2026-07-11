package com.freshtrace.unified.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data @TableName("trace_fertilizer")
public class TraceFertilizer {
    @TableId(type = IdType.ASSIGN_ID) private Long id;
    private Long traceId;
    private LocalDate fertilizeDate;
    private String fertilizerName;
    private BigDecimal dosage;
    private String fertilizeMethod;
    private String description;
    private String operator;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
