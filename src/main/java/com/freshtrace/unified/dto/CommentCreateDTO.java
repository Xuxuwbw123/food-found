package com.freshtrace.unified.dto;

import lombok.Data;

@Data
public class CommentCreateDTO {
    private Long productId;
    private Long orderId;
    private Long orderItemId;
    private Integer rating;
    private String content;
    private String images;
    private Integer isAnonymous;
}
