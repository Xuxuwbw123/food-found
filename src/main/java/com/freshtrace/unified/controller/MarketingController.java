package com.freshtrace.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshtrace.unified.common.PageQuery;
import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.entity.*;
import com.freshtrace.unified.mapper.MarketingGoodsMapper;
import com.freshtrace.unified.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/marketing")
public class MarketingController {

    @Autowired private MarketingActivityService marketingService;
    @Autowired private MarketingGoodsMapper marketingGoodsMapper;
    @Autowired private ProductService productService;
    @Autowired private SysNoticeService noticeService;

    @GetMapping("")
    public Result<?> list() {
        return Result.success(marketingService.list(new LambdaQueryWrapper<MarketingActivity>()
                .orderByDesc(MarketingActivity::getCreateTime)));
    }

    @PostMapping("")
    public Result<?> add(@RequestBody Map<String, Object> body) {
        MarketingActivity activity = new MarketingActivity();
        activity.setName((String) body.get("name"));
        activity.setType((String) body.get("type"));
        activity.setRule((String) body.getOrDefault("rule", ""));
        if (body.get("startTime") != null) activity.setStartTime(LocalDateTime.parse(((String) body.get("startTime")).replace(" ", "T")));
        if (body.get("endTime") != null) activity.setEndTime(LocalDateTime.parse(((String) body.get("endTime")).replace(" ", "T")));
        activity.setStatus(Integer.parseInt(body.getOrDefault("status", "0").toString()));
        activity.setCreateTime(LocalDateTime.now());
        marketingService.save(activity);

        // 如果状态为1（进行中），发送通知
        if (activity.getStatus() == 1) {
            sendNotification(activity);
        }

        return Result.success("创建成功", Map.of("id", activity.getId()));
    }

    @PutMapping("")
    public Result<?> update(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(body.get("id").toString());
        MarketingActivity old = marketingService.getById(id);
        MarketingActivity activity = new MarketingActivity();
        activity.setId(id);
        activity.setName((String) body.get("name"));
        activity.setType((String) body.get("type"));
        activity.setRule((String) body.getOrDefault("rule", ""));
        if (body.get("startTime") != null) activity.setStartTime(LocalDateTime.parse(((String) body.get("startTime")).replace(" ", "T")));
        if (body.get("endTime") != null) activity.setEndTime(LocalDateTime.parse(((String) body.get("endTime")).replace(" ", "T")));
        Integer newStatus = Integer.parseInt(body.getOrDefault("status", "0").toString());
        activity.setStatus(newStatus);
        marketingService.updateById(activity);

        // 如果状态从未激活变为激活，发送通知
        if (old != null && old.getStatus() != 1 && newStatus == 1) {
            sendNotification(activity);
        }

        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        marketingService.removeById(id);
        marketingGoodsMapper.delete(new LambdaQueryWrapper<MarketingGoods>().eq(MarketingGoods::getActivityId, id));
        return Result.success();
    }

    // 关联商品
    @PostMapping("/{id}/products")
    public Result<?> addProducts(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        List<Long> productIds = ((List<Number>) body.get("productIds")).stream().map(Number::longValue).collect(Collectors.toList());
        for (Long pid : productIds) {
            long count = marketingGoodsMapper.selectCount(new LambdaQueryWrapper<MarketingGoods>()
                    .eq(MarketingGoods::getActivityId, id).eq(MarketingGoods::getProductId, pid));
            if (count == 0) {
                MarketingGoods mg = new MarketingGoods();
                mg.setActivityId(id); mg.setProductId(pid);
                marketingGoodsMapper.insert(mg);
            }
        }
        return Result.success();
    }

    // 获取活动关联的商品
    @GetMapping("/{id}/products")
    public Result<?> getProducts(@PathVariable Long id) {
        List<MarketingGoods> goods = marketingGoodsMapper.selectList(
                new LambdaQueryWrapper<MarketingGoods>().eq(MarketingGoods::getActivityId, id));
        List<Map<String, Object>> result = new ArrayList<>();
        for (MarketingGoods mg : goods) {
            Product p = productService.getById(mg.getProductId());
            if (p != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", mg.getId());
                item.put("productId", p.getId());
                item.put("productName", p.getProductName());
                item.put("mainImage", p.getMainImage());
                item.put("price", p.getPrice());
                result.add(item);
            }
        }
        return Result.success(result);
    }

    // 删除关联商品
    @DeleteMapping("/goods/{goodsId}")
    public Result<?> removeGoods(@PathVariable Long goodsId) {
        marketingGoodsMapper.deleteById(goodsId);
        return Result.success();
    }

    // 获取当前有效的营销活动（用户端）
    @GetMapping("/active")
    public Result<?> getActiveActivities() {
        LocalDateTime now = LocalDateTime.now();
        List<MarketingActivity> activities = marketingService.list(new LambdaQueryWrapper<MarketingActivity>()
                .eq(MarketingActivity::getStatus, 1)
                .le(MarketingActivity::getStartTime, now)
                .ge(MarketingActivity::getEndTime, now));
        return Result.success(activities);
    }

    // 获取商品适用的营销活动（用户端）
    @GetMapping("/product/{productId}")
    public Result<?> getProductActivities(@PathVariable Long productId) {
        LocalDateTime now = LocalDateTime.now();
        List<MarketingGoods> goods = marketingGoodsMapper.selectList(
                new LambdaQueryWrapper<MarketingGoods>().eq(MarketingGoods::getProductId, productId));
        List<MarketingActivity> result = new ArrayList<>();
        for (MarketingGoods mg : goods) {
            MarketingActivity activity = marketingService.getById(mg.getActivityId());
            if (activity != null && activity.getStatus() == 1
                    && activity.getStartTime() != null && activity.getEndTime() != null
                    && activity.getStartTime().isBefore(now) && activity.getEndTime().isAfter(now)) {
                result.add(activity);
            }
        }
        return Result.success(result);
    }

    // 计算营销折扣
    public static BigDecimal calculateDiscount(MarketingActivity activity, BigDecimal originalPrice, BigDecimal orderTotal) {
        if (activity == null || activity.getRule() == null) return BigDecimal.ZERO;
        String rule = activity.getRule();
        try {
            if ("discount".equals(activity.getType())) {
                // 折扣: {"discount":0.8} 表示8折
                double discount = Double.parseDouble(rule.replaceAll(".*\"discount\":\\s*([0-9.]+).*", "$1"));
                return originalPrice.multiply(BigDecimal.valueOf(1 - discount));
            } else if ("full_reduce".equals(activity.getType())) {
                // 满减: {"minAmount":100,"reduceAmount":20}
                double minAmount = Double.parseDouble(rule.replaceAll(".*\"minAmount\":\\s*([0-9.]+).*", "$1"));
                double reduceAmount = Double.parseDouble(rule.replaceAll(".*\"reduceAmount\":\\s*([0-9.]+).*", "$1"));
                if (orderTotal.doubleValue() >= minAmount) {
                    return BigDecimal.valueOf(reduceAmount);
                }
            }
        } catch (Exception ignored) {}
        return BigDecimal.ZERO;
    }

    private void sendNotification(MarketingActivity activity) {
        try {
            SysNotice notice = new SysNotice();
            notice.setUserId(0L); // 发给所有用户
            notice.setNoticeType("marketing");
            notice.setTitle("新营销活动上线");
            notice.setContent("【" + activity.getName()  + "】活动已开始，快来参与吧！");
            notice.setIsRead(0);
            notice.setCreateTime(LocalDateTime.now());
            noticeService.save(notice);
        } catch (Exception ignored) {}
    }
}
