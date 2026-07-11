package com.freshtrace.unified.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class CreateOrderDTO {
    private Long addressId;
    private List<Long> cartItemIds;
    private String remark;
    private Long couponId;

    // Direct order fields (buy now flow)
    private List<OrderItemDTO> items;
    private Map<String, Object> address;

    @Data
    public static class OrderItemDTO {
        private Long productId;
        private String productName;
        private String productImage;
        private BigDecimal price;
        private Integer quantity;
        private String unit;
        private Long traceId;
    }
}
