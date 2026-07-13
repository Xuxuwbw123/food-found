package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@TableName("shop_follow")
public class ShopFollow {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private Long farmerId;
    private java.time.LocalDateTime createTime;
}
