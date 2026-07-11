package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("customer_service_log")
public class CustomerServiceLog {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long afterSalesId;
    private Long orderId;
    private Long userId;
    private Long operatorId;
    private String operatorName;
    private Integer operatorType;
    private Integer msgType;
    private String content;
    private String imageUrls;
    private LocalDateTime createTime;
}
