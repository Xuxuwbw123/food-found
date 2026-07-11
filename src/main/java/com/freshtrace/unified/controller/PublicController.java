package com.freshtrace.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshtrace.unified.common.PageQuery;
import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.entity.*;
import com.freshtrace.unified.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired private BannerService bannerService;
    @Autowired private CategoryService categoryService;
    @Autowired private ProductService productService;
    @Autowired private FarmerService farmerService;

    @GetMapping("/banner/list")
    public Result<?> bannerList() {
        List<Banner> list = bannerService.list(new LambdaQueryWrapper<Banner>()
                .eq(Banner::getStatus, 1).eq(Banner::getDeleted, 0).orderByAsc(Banner::getSort));
        Map<String, Object> data = new HashMap<>();
        data.put("records", list);
        data.put("total", list.size());
        return Result.success(data);
    }

    @GetMapping("/category/list")
    public Result<?> categoryList() {
        List<Category> list = categoryService.list(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1).eq(Category::getDeleted, 0).orderByAsc(Category::getSort));
        return Result.success(list);
    }

    @GetMapping("/category/first-level")
    public Result<?> firstLevelCategories() {
        List<Category> list = categoryService.list(new LambdaQueryWrapper<Category>()
                .and(w -> w.isNull(Category::getParentId).or().eq(Category::getParentId, 0))
                .eq(Category::getStatus, 1).eq(Category::getDeleted, 0).orderByAsc(Category::getSort));
        return Result.success(list);
    }

    @GetMapping("/category/tree")
    public Result<?> categoryTree() {
        List<Category> list = categoryService.list(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1).eq(Category::getDeleted, 0).orderByAsc(Category::getSort));
        return Result.success(list);
    }

    @GetMapping("/product/list")
    public Result<?> productList(PageQuery pageQuery,
            @RequestParam(required = false) Long categoryId) {
        LambdaQueryWrapper<Product> qw = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1).eq(Product::getAuditStatus, 1).eq(Product::getDeleted, 0);
        if (categoryId != null) qw.eq(Product::getCategoryId, categoryId);
        qw.orderByAsc(Product::getSort).orderByDesc(Product::getSales);
        Page<Product> page = productService.page(pageQuery.toPage(), qw);
        return Result.success(page);
    }

    @GetMapping("/products/home")
    public Result<?> homeProducts() {
        List<Product> list = productService.list(new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1).eq(Product::getAuditStatus, 1).eq(Product::getDeleted, 0)
                .orderByAsc(Product::getSort).orderByDesc(Product::getSales).last("LIMIT 12"));
        return Result.success(list);
    }

    @GetMapping("/products/detail/{id}")
    public Result<?> productDetail(@PathVariable Long id) {
        Product p = productService.getById(id);
        if (p == null || p.getDeleted() == 1) return Result.error(404, "商品不存在");
        return Result.success(p);
    }

    @GetMapping("/product/search")
    public Result<?> productSearch(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "default") String sort,
            @RequestParam(required = false, defaultValue = "0") Double minPrice,
            @RequestParam(required = false, defaultValue = "999999") Double maxPrice,
            PageQuery pageQuery) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.success(new Page<>());
        }
        LambdaQueryWrapper<Product> qw = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1).eq(Product::getAuditStatus, 1).eq(Product::getDeleted, 0)
                .and(w -> w.like(Product::getProductName, keyword).or().like(Product::getDescription, keyword))
                .between(Product::getPrice, minPrice, maxPrice);
        if (categoryId != null) qw.eq(Product::getCategoryId, categoryId);
        if ("price_asc".equals(sort)) qw.orderByAsc(Product::getPrice);
        else if ("price_desc".equals(sort)) qw.orderByDesc(Product::getPrice);
        else if ("newest".equals(sort)) qw.orderByDesc(Product::getCreateTime);
        else qw.orderByDesc(Product::getSales);
        Page<Product> page = productService.page(pageQuery.toPage(), qw);
        return Result.success(page);
    }

    @GetMapping("/product/recommend")
    public Result<?> recommendProducts() {
        List<Product> list = productService.list(new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1).eq(Product::getAuditStatus, 1).eq(Product::getDeleted, 0)
                .eq(Product::getIsRecommend, 1).orderByDesc(Product::getSales).last("LIMIT 8"));
        return Result.success(list);
    }

    @GetMapping("/product/related/{productId}")
    public Result<?> relatedProducts(@PathVariable Long productId) {
        Product p = productService.getById(productId);
        if (p == null) return Result.success(Collections.emptyList());
        List<Product> list = productService.list(new LambdaQueryWrapper<Product>()
                .eq(Product::getCategoryId, p.getCategoryId()).ne(Product::getId, productId)
                .eq(Product::getStatus, 1).eq(Product::getAuditStatus, 1).eq(Product::getDeleted, 0)
                .orderByDesc(Product::getSales).last("LIMIT 6"));
        return Result.success(list);
    }

    @GetMapping("/farmer/list")
    public Result<?> farmerList() {
        List<Farmer> list = farmerService.list(new LambdaQueryWrapper<Farmer>()
                .eq(Farmer::getDeleted, 0).orderByDesc(Farmer::getCreateTime));
        return Result.success(list);
    }

    @GetMapping("/farmer/detail/{id}")
    public Result<?> farmerDetail(@PathVariable Long id) {
        Farmer f = farmerService.getById(id);
        if (f == null || f.getDeleted() == 1) return Result.error(404, "农户不存在");
        return Result.success(f);
    }
}
