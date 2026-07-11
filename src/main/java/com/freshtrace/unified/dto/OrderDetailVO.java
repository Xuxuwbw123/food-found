package com.freshtrace.unified.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long farmerId;
    private String farmerName;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private BigDecimal freightAmount;
    private BigDecimal discountAmount;
    private Integer totalQuantity;
    private Integer payType;
    private Integer payStatus;
    private Integer orderStatus;
    private String orderStatusName;
    private Integer deliveryType;
    private String receiverName;
    private String receiverPhone;
    private String receiverProvince;
    private String receiverCity;
    private String receiverDistrict;
    private String receiverAddress;
    private String logisticsNo;
    private String logisticsCompany;
    private String orderRemark;
    private Integer isComment;
    private LocalDateTime payTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime receiveTime;
    private LocalDateTime finishTime;
    private LocalDateTime cancelTime;
    private String cancelReason;
    private LocalDateTime createTime;
    private List<OrderItemVO> items;
    private PaymentVO payment;
    private List<OrderLogVO> logs;
    private List<LogisticsTrackVO> tracks;

    @Data
    public static class PaymentVO {
        private String paymentNo;
        private Integer payType;
        private BigDecimal payAmount;
        private Integer payStatus;
        private LocalDateTime payTime;
        private BigDecimal refundAmount;
        private LocalDateTime refundTime;
    }

    @Data
    public static class OrderLogVO {
        private String remark;
        private Integer operatorType;
        private LocalDateTime createTime;
    }

    @Data
    public static class LogisticsTrackVO {
        private String station;
        private String location;
        private String description;
        private LocalDateTime trackTime;
    }

    @Data
    public static class OrderItemVO {
        private Long id;
        private Long productId;
        private String productName;
        private String productImage;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal totalAmount;
        private Long traceId;
        private Integer isComment;
    }
}
