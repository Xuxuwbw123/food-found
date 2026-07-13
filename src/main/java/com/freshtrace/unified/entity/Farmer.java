package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("farmer")
public class Farmer {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private String farmerName;
    private String farmerLogo;
    private String contactPerson;
    private String contactPhone;
    private String province;
    private String city;
    private String district;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private BigDecimal farmArea;
    private String farmDescription;
    private String mainProducts;
    private Integer auditStatus;
    private String auditRemark;
    private LocalDateTime auditTime;
    private Long auditorId;
    private Integer level;
    private BigDecimal score;
    private Integer salesCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
