package com.freshtrace.unified.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductVO {
    private Long id;
    private String productNo;
    private String productName;
    private Long categoryId;
    private String categoryName;
    private Long farmerId;
    private String farmerName;
    private String mainImage;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer sales;
    private String unit;
    private BigDecimal weight;
    private String originPlace;
    private String description;
    private String specification;
    private Integer isTraceable;
    private Long traceId;
    private Integer status;
    private Integer isRecommend;
    private Integer isNew;
    private Integer isHot;
    private Integer commentCount;
    private BigDecimal goodRate;
    private List<String> images;
}
