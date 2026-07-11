package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("after_sales_order")
public class AfterSalesOrder {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String afterSalesNo;
    private Long orderId;
    private String orderNo;
    private Long orderItemId;
    private Long userId;
    private Long farmerId;
    private Long productId;
    private String productName;
    private Integer afterSalesType;
    private String reason;
    private String description;
    private String evidenceImages;
    private BigDecimal refundAmount;
    private String returnAddress;
    private String returnLogisticsNo;
    private String returnLogisticsCompany;
    private Integer status;
    private Long adminId;
    private String adminRemark;
    private String userRemark;
    private LocalDateTime applyTime;
    private LocalDateTime auditTime;
    private LocalDateTime completeTime;
    private LocalDateTime closeTime;
    private String closeReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
