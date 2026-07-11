package com.freshtrace.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.common.UserContext;
import com.freshtrace.unified.entity.ProductComment;
import com.freshtrace.unified.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired private CommentService commentService;

    @PostMapping("/direct")
    public Result<Void> directComment(@RequestBody Map<String, Object> body) {
        Long userId = UserContext.getUserId();
        Long productId = Long.valueOf(body.get("productId").toString());
        int rating = Integer.parseInt(body.getOrDefault("rating", "5").toString());
        String content = (String) body.getOrDefault("content", "");
        if (rating < 1 || rating > 5) return Result.fail("评分1-5星");
        if (content.trim().isEmpty()) return Result.fail("请输入评论内容");

        ProductComment comment = new ProductComment();
        comment.setProductId(productId); comment.setUserId(userId); comment.setRating(rating);
        comment.setContent(content.trim()); comment.setImages(""); comment.setStatus(0);
        comment.setCreateTime(LocalDateTime.now());
        commentService.save(comment);
        return Result.ok();
    }

    @PostMapping("/add")
    public Result<Void> addComment(@RequestBody Map<String, Object> body) {
        Long userId = UserContext.getUserId();
        Long orderId = Long.valueOf(body.get("orderId").toString());
        Long productId = Long.valueOf(body.get("productId").toString());
        int rating = Integer.parseInt(body.getOrDefault("rating", "5").toString());
        String content = (String) body.getOrDefault("content", "");
        if (rating < 1 || rating > 5) return Result.fail("评分1-5星");

        ProductComment comment = new ProductComment();
        comment.setProductId(productId); comment.setUserId(userId); comment.setOrderId(orderId);
        comment.setRating(rating); comment.setContent(content);
        Object images = body.get("images");
        comment.setImages(images instanceof List ? String.join(",", (List<String>) images) : "");
        comment.setStatus(0); comment.setCreateTime(LocalDateTime.now());
        commentService.save(comment);
        return Result.ok();
    }

    @GetMapping("/product/{productId}")
    public Result<List<ProductComment>> productComments(@PathVariable Long productId) {
        // 所有评论公开显示，被管理员删除的不显示（已从数据库移除）
        LambdaQueryWrapper<ProductComment> qw = new LambdaQueryWrapper<ProductComment>()
                .eq(ProductComment::getProductId, productId)
                .orderByDesc(ProductComment::getCreateTime);
        return Result.ok(commentService.list(qw));
    }

    @GetMapping("/mine")
    public Result<List<ProductComment>> myComments() {
        Long userId = UserContext.getUserId();
        return Result.ok(commentService.list(new LambdaQueryWrapper<ProductComment>()
                .eq(ProductComment::getUserId, userId).orderByDesc(ProductComment::getCreateTime)));
    }
}
