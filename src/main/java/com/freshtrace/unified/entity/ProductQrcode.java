package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@TableName("product_qrcode")
public class ProductQrcode {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long productId;
    private Long traceId;
    private Long orderId;
    private String qrcodeContent;
    private String qrcodeImage;
    private Integer scanCount;
    private java.time.LocalDateTime firstScanTime;
    private String firstScanIp;
    private Integer status;
    private java.time.LocalDateTime createTime;
}
