package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String productNo;
    private String productName;
    private Long categoryId;
    private Long farmerId;
    private String mainImage;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal costPrice;
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
    private Integer isPresale;
    private LocalDate presaleStart;
    private LocalDate presaleEnd;
    private Integer auditStatus;
    private String auditRemark;
    private Integer isRecommend;
    private Integer isNew;
    private Integer isHot;
    private Integer sort;
    private Integer viewCount;
    private Integer collectCount;
    private Integer commentCount;
    private BigDecimal goodRate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
