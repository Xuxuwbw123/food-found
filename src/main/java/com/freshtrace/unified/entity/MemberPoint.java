package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("member_point")
public class MemberPoint {
    @TableId(type = IdType.INPUT)
    private Long userId;
    private Integer totalPoint;
    private Integer availablePoint;
    private Integer freezePoint;
    private LocalDateTime updateTime;
}
