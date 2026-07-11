package com.freshtrace.unified.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentVO {
    private Long id;
    private Long productId;
    private Long userId;
    private String nickname;
    private String avatar;
    private Long orderId;
    private Integer rating;
    private String content;
    private String images;
    private Integer isAnonymous;
    private Integer likeCount;
    private LocalDateTime createTime;
}
