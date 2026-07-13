package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("order_info")
public class OrderInfo {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String orderNo;
    private Long userId;
    private Long farmerId;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private BigDecimal planAmount;
    private BigDecimal actualAmount;
    private Long marketingId;
    private Long couponId;
    private BigDecimal freightAmount;
    private BigDecimal discountAmount;
    private Integer totalQuantity;
    private Integer payType;
    private LocalDateTime payTime;
    private Integer orderStatus;
    private Integer presaleStatus;
    private Long traceId;
    private Integer deliveryType;
    private LocalDateTime deliveryTime;
    private LocalDateTime receiveTime;
    private LocalDateTime finishTime;
    private LocalDateTime cancelTime;
    private String cancelReason;
    private String receiverName;
    private String receiverPhone;
    private String receiverProvince;
    private String receiverCity;
    private String receiverDistrict;
    private String receiverAddress;
    private String logisticsNo;
    private String logisticsCompany;
    private String orderRemark;
    private String adminRemark;
    private Integer isInvoiced;
    private Integer invoiceType;
    private String invoiceTitle;
    private Integer isComment;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private List<OrderItem> items;

    @TableField(exist = false)
    private List<OrderLog> logs;
}
