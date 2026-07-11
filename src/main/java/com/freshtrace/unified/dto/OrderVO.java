package com.freshtrace.unified.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long farmerId;
    private String farmerName;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Integer totalQuantity;
    private Integer payType;
    private Integer orderStatus;
    private String orderStatusName;
    private Integer deliveryType;
    private String logisticsNo;
    private String logisticsCompany;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String orderRemark;
    private Integer isComment;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime deliveryTime;
    private List<OrderItemVO> items;

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
