package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_notice")
public class SysNotice {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private String noticeType;
    private String title;
    private String content;
    private Long relationId;
    private Integer isRead;
    private LocalDateTime createTime;
}
