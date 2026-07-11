package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_coupon")
public class UserCoupon {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private Long couponId;
    private String status;
    private Long orderId;
    private LocalDateTime takeTime;
    private LocalDateTime useTime;
}
