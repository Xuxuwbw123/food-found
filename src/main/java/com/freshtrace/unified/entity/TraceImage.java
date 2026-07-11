package com.freshtrace.unified.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
@Data @TableName("trace_image")
public class TraceImage {
    @TableId(type = IdType.ASSIGN_ID) private Long id;
    private Long traceId;
    private String imageType;
    private String imageUrl;
    private String imageDesc;
    private Integer sort;
    private LocalDateTime createTime;
}
