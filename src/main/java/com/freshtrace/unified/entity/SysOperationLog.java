package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_operation_log")
public class SysOperationLog {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private String username;
    private String module;
    private String action;
    private String target;
    private String reqMethod;
    private String reqPath;
    private String reqIp;
    private String reqParams;
    private String result;
    private Integer duration;
    private LocalDateTime createTime;
}
