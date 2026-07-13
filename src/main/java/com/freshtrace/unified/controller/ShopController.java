package com.freshtrace.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.common.UserContext;
import com.freshtrace.unified.entity.*;
import com.freshtrace.unified.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
public class ShopController {

    @Autowired private FarmerService farmerService;
    @Autowired private ShopFollowService followService;
    @Autowired private ProductService productService;
    @Autowired private HarvestCalendarService calendarService;
    @Autowired private HarvestSubscribeService subscribeService;
    @Autowired private FarmUpdateService farmUpdateService;
    @Autowired private QualificationCertService certService;
    @Autowired private SysNoticeService noticeService;

    // ============ 农户店铺 ============
    @GetMapping("/api/shop/{farmerId}")
    public Result<?> getShop(@PathVariable Long farmerId) {
        Farmer farmer = farmerService.getById(farmerId);
        if (farmer == null) return Result.error(404, "店铺不存在");
        Map<String, Object> data = new HashMap<>();
        data.put("farmer", farmer);
        data.put("products", productService.list(new LambdaQueryWrapper<Product>()
                .eq(Product::getFarmerId, farmerId).eq(Product::getStatus, 1).eq(Product::getDeleted, 0)));
        data.put("updates", farmUpdateService.list(new LambdaQueryWrapper<FarmUpdate>()
                .eq(FarmUpdate::getFarmerId, farmerId).orderByDesc(FarmUpdate::getCreateTime).last("LIMIT 10")));
        data.put("certs", certService.list(new LambdaQueryWrapper<QualificationCert>()
                .eq(QualificationCert::getFarmerId, farmerId)));
        Long userId = null;
        try { userId = UserContext.getUserId(); } catch (Exception ignored) {}
        if (userId != null) {
            long followCount = followService.count(new LambdaQueryWrapper<ShopFollow>()
                    .eq(ShopFollow::getUserId, userId).eq(ShopFollow::getFarmerId, farmerId));
            data.put("followed", followCount > 0);
        }
        data.put("followerCount", followService.count(new LambdaQueryWrapper<ShopFollow>()
                .eq(ShopFollow::getFarmerId, farmerId)));
        return Result.success(data);
    }

    // ============ 关注/取关 ============
    @PostMapping("/api/shop/follow/{farmerId}")
    public Result<?> follow(@PathVariable Long farmerId) {
        Long userId = UserContext.getUserId();
        long count = followService.count(new LambdaQueryWrapper<ShopFollow>()
                .eq(ShopFollow::getUserId, userId).eq(ShopFollow::getFarmerId, farmerId));
        if (count > 0) return Result.error(400, "已关注");
        ShopFollow follow = new ShopFollow();
        follow.setUserId(userId); follow.setFarmerId(farmerId);
        follow.setCreateTime(LocalDateTime.now());
        followService.save(follow);
        return Result.success();
    }

    @DeleteMapping("/api/shop/unfollow/{farmerId}")
    public Result<?> unfollow(@PathVariable Long farmerId) {
        Long userId = UserContext.getUserId();
        followService.remove(new LambdaQueryWrapper<ShopFollow>()
                .eq(ShopFollow::getUserId, userId).eq(ShopFollow::getFarmerId, farmerId));
        return Result.success();
    }

    @GetMapping("/api/shop/follow/list")
    public Result<?> myFollows() {
        Long userId = UserContext.getUserId();
        return Result.success(followService.list(new LambdaQueryWrapper<ShopFollow>()
                .eq(ShopFollow::getUserId, userId).orderByDesc(ShopFollow::getCreateTime)));
    }

    // ============ 上市日历 ============
    @GetMapping("/api/calendar/list")
    public Result<?> calendarList() {
        return Result.success(calendarService.list(new LambdaQueryWrapper<HarvestCalendar>()
                .eq(HarvestCalendar::getStatus, 1).orderByAsc(HarvestCalendar::getHarvestMonth)));
    }

    @PostMapping("/api/calendar/subscribe/{calendarId}")
    public Result<?> subscribe(@PathVariable Long calendarId) {
        Long userId = UserContext.getUserId();
        long count = subscribeService.count(new LambdaQueryWrapper<HarvestSubscribe>()
                .eq(HarvestSubscribe::getUserId, userId).eq(HarvestSubscribe::getCalendarId, calendarId));
        if (count > 0) return Result.error(400, "已订阅");
        HarvestSubscribe sub = new HarvestSubscribe();
        sub.setUserId(userId); sub.setCalendarId(calendarId);
        sub.setCreateTime(LocalDateTime.now());
        subscribeService.save(sub);
        return Result.success();
    }

    @DeleteMapping("/api/calendar/unsubscribe/{calendarId}")
    public Result<?> unsubscribe(@PathVariable Long calendarId) {
        Long userId = UserContext.getUserId();
        subscribeService.remove(new LambdaQueryWrapper<HarvestSubscribe>()
                .eq(HarvestSubscribe::getUserId, userId).eq(HarvestSubscribe::getCalendarId, calendarId));
        return Result.success();
    }
}
