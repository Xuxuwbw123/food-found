package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@TableName("qrcode_scan_log")
public class QrcodeScanLog {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long qrcodeId;
    private Long userId;
    private String scanIp;
    private java.time.LocalDateTime scanTime;
}
