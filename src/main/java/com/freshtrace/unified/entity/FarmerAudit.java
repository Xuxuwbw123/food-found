package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("farmer_audit")
public class FarmerAudit {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long farmerId;
    private Long auditorId;
    private String auditorName;
    private Integer auditStatus;
    private String auditRemark;
    private LocalDateTime auditTime;
    private LocalDateTime createTime;
}
