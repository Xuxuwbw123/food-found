package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sys_location")
public class SysLocation {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String locationName;
    private String locationType;
    private Long farmerId;
    private Long userId;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String province;
    private String city;
    private String district;
    private Integer status;
    private LocalDateTime createTime;
}
