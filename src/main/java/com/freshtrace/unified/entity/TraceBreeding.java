package com.freshtrace.unified.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data @TableName("trace_breeding")
public class TraceBreeding {
    @TableId(type = IdType.ASSIGN_ID) private Long id;
    private Long traceId;
    private String breedType;
    private LocalDate breedDate;
    private String species;
    private String description;
    private String operator;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
