package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@TableName("group_buy_record")
public class GroupBuyRecord {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long groupBuyId;
    private Long userId;
    private Long orderId;
    private String status;
    private java.time.LocalDateTime createTime;
}
