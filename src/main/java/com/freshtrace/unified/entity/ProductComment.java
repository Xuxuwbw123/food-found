package com.freshtrace.unified.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("product_comment")
public class ProductComment {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long productId;
    private Long userId;
    private Long orderId;
    private Long orderItemId;
    private Integer rating;
    private String content;
    private String images;
    private Integer isAnonymous;
    private Integer likeCount;
    private Integer replyCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
