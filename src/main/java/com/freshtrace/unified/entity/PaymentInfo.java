package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment_info")
public class PaymentInfo {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long orderId;
    private String orderNo;
    private Long userId;
    private String paymentNo;
    private Integer payType;
    private BigDecimal payAmount;
    private Integer payStatus;
    private LocalDateTime payTime;
    private String thirdPartyNo;
    private BigDecimal refundAmount;
    private LocalDateTime refundTime;
    private String refundReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
