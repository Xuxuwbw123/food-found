package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("traceability")
public class Traceability {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String traceCode;
    private Long productId;
    private Long farmerId;
    private String batchNo;
    private String productName;
    private String originPlace;
    private String farmName;
    private String responsiblePerson;
    private String responsiblePhone;
    private BigDecimal plantingArea;
    private String seedSource;
    private LocalDate plantingDate;
    private LocalDate expectedHarvestDate;
    private LocalDate actualHarvestDate;
    private Integer shelfLife;
    private String storageCondition;
    private Integer totalNodes;
    private String currentStage;
    private String qrcodeUrl;
    private Integer status;
    private Integer auditStatus;
    private Integer scanCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
