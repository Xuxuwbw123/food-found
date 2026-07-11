package com.freshtrace.unified.controller;

import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.dto.CommentCreateDTO;
import com.freshtrace.unified.dto.CommentVO;
import com.freshtrace.unified.dto.ProductVO;
import com.freshtrace.unified.entity.Product;
import com.freshtrace.unified.service.CommentService;
import com.freshtrace.unified.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired private ProductService productService;
    @Autowired private CommentService commentService;

    @GetMapping
    public Result<Page<Product>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort) {
        return Result.ok(productService.list(pageNum, pageSize, categoryId, keyword, sort));
    }

    @GetMapping("/{id}")
    public Result<ProductVO> detail(@PathVariable Long id) {
        try { return Result.ok(productService.getDetail(id)); }
        catch (Exception e) { return Result.fail(e.getMessage()); }
    }

    @GetMapping("/{id}/comments")
    public Result<Page<CommentVO>> comments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer rating) {
        return Result.ok(commentService.listByProduct(id, pageNum, pageSize, rating));
    }

    @PostMapping("/{id}/comments")
    public Result<Void> createComment(@RequestBody CommentCreateDTO dto) {
        try { commentService.create(dto); return Result.ok(); }
        catch (Exception e) { return Result.fail(e.getMessage()); }
    }
}
