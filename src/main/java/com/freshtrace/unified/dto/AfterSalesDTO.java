package com.freshtrace.unified.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AfterSalesDTO {
    private Long orderItemId;
    private Integer afterSalesType;
    private String reason;
    private String description;
    private String evidenceImages;
    private BigDecimal refundAmount;
}
