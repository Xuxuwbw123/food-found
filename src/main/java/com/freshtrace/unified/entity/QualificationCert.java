package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@TableName("qualification_cert")
public class QualificationCert {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long farmerId;
    private Long productId;
    private Long traceId;
    private String certType;
    private String certName;
    private String certImage;
    private String certNo;
    private String issueOrg;
    private java.time.LocalDate issueDate;
    private java.time.LocalDate expireDate;
    private Integer status;
    private java.time.LocalDateTime createTime;
}
