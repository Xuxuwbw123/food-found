package com.freshtrace.unified.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
@Data @TableName("trace_logistics")
public class TraceLogistics {
    @TableId(type = IdType.ASSIGN_ID) private Long id;
    private Long traceId;
    private String logisticsNo;
    private String carrierCompany;
    private String driverName;
    private String driverPhone;
    private Integer transportStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
