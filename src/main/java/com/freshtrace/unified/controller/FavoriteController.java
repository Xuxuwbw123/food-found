package com.freshtrace.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.common.UserContext;
import com.freshtrace.unified.entity.ProductFavorite;
import com.freshtrace.unified.service.ProductFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    @Autowired private ProductFavoriteService favoriteService;

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Map<String, Object> body) {
        Long userId = UserContext.getUserId();
        Long productId = Long.valueOf(body.get("productId").toString());
        ProductFavorite fav = new ProductFavorite();
        fav.setUserId(userId); fav.setProductId(productId); fav.setCreateTime(LocalDateTime.now());
        favoriteService.save(fav);
        return Result.ok();
    }

    @DeleteMapping("/remove/{productId}")
    public Result<Void> remove(@PathVariable Long productId) {
        Long userId = UserContext.getUserId();
        favoriteService.remove(new LambdaQueryWrapper<ProductFavorite>()
                .eq(ProductFavorite::getUserId, userId).eq(ProductFavorite::getProductId, productId));
        return Result.ok();
    }

    @GetMapping("/list")
    public Result<List<ProductFavorite>> list() {
        Long userId = UserContext.getUserId();
        return Result.ok(favoriteService.list(new LambdaQueryWrapper<ProductFavorite>()
                .eq(ProductFavorite::getUserId, userId).orderByDesc(ProductFavorite::getCreateTime)));
    }

    @GetMapping("/check/{productId}")
    public Result<Map<String, Boolean>> check(@PathVariable Long productId) {
        Long userId = UserContext.getUserId();
        long count = favoriteService.count(new LambdaQueryWrapper<ProductFavorite>()
                .eq(ProductFavorite::getUserId, userId).eq(ProductFavorite::getProductId, productId));
        Map<String, Boolean> data = new HashMap<>();
        data.put("faved", count > 0);
        return Result.ok(data);
    }
}
