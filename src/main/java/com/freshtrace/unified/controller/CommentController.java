package com.freshtrace.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.common.UserContext;
import com.freshtrace.unified.entity.ProductComment;
import com.freshtrace.unified.entity.SysUser;
import com.freshtrace.unified.mapper.SysUserMapper;
import com.freshtrace.unified.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired private CommentService commentService;
    @Autowired private SysUserMapper userMapper;

    /**
     * 把请求体里的 images 字段(可能是 List<String> / String / null)统一转成
     * 逗号分隔的 URL 字符串存到 product_comment.images 字段。
     */
    @SuppressWarnings("unchecked")
    private String extractImages(Object raw) {
        if (raw == null) return "";
        if (raw instanceof String) return ((String) raw).trim();
        if (raw instanceof List) {
            List<String> list = (List<String>) raw;
            return String.join(",", list.stream().filter(s -> s != null && !s.isBlank()).toList());
        }
        return "";
    }

    /**
     * 给评论列表补 user 信息(nickname / username),避免前端展示"匿名用户"。
     * 一次查出所有 userId 对应的 SysUser,内存里 join,避免 N+1 查询。
     */
    private List<Map<String, Object>> enrichWithUser(List<ProductComment> comments) {
        if (comments == null || comments.isEmpty()) return List.of();
        Set<Long> userIds = comments.stream()
                .map(ProductComment::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, SysUser> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u));
        List<Map<String, Object>> result = new ArrayList<>(comments.size());
        for (ProductComment c : comments) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", c.getId());
            row.put("productId", c.getProductId());
            row.put("userId", c.getUserId());
            row.put("orderId", c.getOrderId());
            row.put("orderItemId", c.getOrderItemId());
            row.put("rating", c.getRating());
            row.put("content", c.getContent());
            row.put("images", c.getImages());
            row.put("isAnonymous", c.getIsAnonymous());
            row.put("likeCount", c.getLikeCount());
            row.put("replyCount", c.getReplyCount());
            row.put("status", c.getStatus());
            row.put("createTime", c.getCreateTime());
            row.put("updateTime", c.getUpdateTime());
            SysUser u = c.getUserId() != null ? userMap.get(c.getUserId()) : null;
            if (u != null) {
                // 匿名评论脱敏
                if (c.getIsAnonymous() != null && c.getIsAnonymous() == 1) {
                    row.put("nickname", "匿名用户");
                } else {
                    row.put("nickname", u.getNickname());
                    row.put("username", u.getUsername());
                    row.put("avatar", u.getAvatar());
                }
            }
            result.add(row);
        }
        return result;
    }

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
        comment.setContent(content.trim());
        comment.setImages(extractImages(body.get("images")));
        comment.setIsAnonymous(0);
        comment.setStatus(0); comment.setCreateTime(LocalDateTime.now());
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
        comment.setImages(extractImages(body.get("images")));
        comment.setIsAnonymous(0);
        comment.setStatus(0); comment.setCreateTime(LocalDateTime.now());
        commentService.save(comment);
        return Result.ok();
    }

    @GetMapping("/product/{productId}")
    public Result<List<Map<String, Object>>> productComments(@PathVariable Long productId) {
        // 公开评论列表:显示待审核(0)+已通过(1),只过滤掉被拒绝的(2)
        // 这样用户提交后立刻能看到自己那条,管理员拒绝掉的才隐藏
        LambdaQueryWrapper<ProductComment> qw = new LambdaQueryWrapper<ProductComment>()
                .eq(ProductComment::getProductId, productId)
                .ne(ProductComment::getStatus, 2)
                .orderByDesc(ProductComment::getCreateTime);
        List<ProductComment> raw = commentService.list(qw);
        return Result.ok(enrichWithUser(raw));
    }

    @GetMapping("/mine")
    public Result<List<Map<String, Object>>> myComments() {
        Long userId = UserContext.getUserId();
        List<ProductComment> raw = commentService.list(new LambdaQueryWrapper<ProductComment>()
                .eq(ProductComment::getUserId, userId).orderByDesc(ProductComment::getCreateTime));
        return Result.ok(enrichWithUser(raw));
    }
}
