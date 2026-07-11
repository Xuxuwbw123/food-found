package com.freshtrace.unified.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AfterSalesVO {
    private Long id;
    private String afterSalesNo;
    private Long orderId;
    private String orderNo;
    private Long orderItemId;
    private Long userId;
    private Long farmerId;
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private Integer afterSalesType;
    private String reason;
    private String description;
    private String evidenceImages;
    private BigDecimal refundAmount;
    private String returnAddress;
    private String returnLogisticsNo;
    private String returnLogisticsCompany;
    private Integer status;
    private String adminRemark;
    private String userRemark;
    private LocalDateTime applyTime;
    private LocalDateTime auditTime;
    private LocalDateTime completeTime;
    private LocalDateTime closeTime;
    private List<ServiceLogVO> serviceLogs;

    @Data
    public static class ServiceLogVO {
        private String content;
        private String imageUrls;
        private Integer msgType;
        private Integer operatorType;
        private String operatorName;
        private LocalDateTime createTime;
    }
}
