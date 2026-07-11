package com.freshtrace.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshtrace.unified.common.PageQuery;
import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.entity.*;
import com.freshtrace.unified.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NoticePointController {

    @Autowired private SysNoticeService noticeService;
    @Autowired private MemberPointService memberPointService;
    @Autowired private PointLogService pointLogService;
    @Autowired private UserFootprintService footprintService;
    @Autowired private UserCouponService userCouponService;
    @Autowired private CouponService couponService;

    // ============ 通知 ============
    @GetMapping("/notice/list")
    public Result<?> noticeList(HttpServletRequest request, PageQuery pageQuery,
            @RequestParam(required = false) String type) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<SysNotice> qw = new LambdaQueryWrapper<SysNotice>()
                .eq(SysNotice::getUserId, userId);
        if (type != null && !type.isEmpty()) qw.eq(SysNotice::getNoticeType, type);
        qw.orderByAsc(SysNotice::getIsRead).orderByDesc(SysNotice::getCreateTime);
        return Result.success(noticeService.page(pageQuery.toPage(), qw));
    }

    @PutMapping("/notice/read/{id}")
    public Result<?> readNotice(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        noticeService.update(new LambdaUpdateWrapper<SysNotice>()
                .eq(SysNotice::getId, id).eq(SysNotice::getUserId, userId).set(SysNotice::getIsRead, 1));
        return Result.success();
    }

    @PutMapping("/notice/read-all")
    public Result<?> readAllNotices(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        noticeService.update(new LambdaUpdateWrapper<SysNotice>()
                .eq(SysNotice::getUserId, userId).eq(SysNotice::getIsRead, 0).set(SysNotice::getIsRead, 1));
        return Result.success();
    }

    @GetMapping("/notice/unread-count")
    public Result<?> unreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        long count = noticeService.count(new LambdaQueryWrapper<SysNotice>()
                .eq(SysNotice::getUserId, userId).eq(SysNotice::getIsRead, 0));
        Map<String, Long> data = new HashMap<>();
        data.put("count", count);
        return Result.success(data);
    }

    // ============ 积分 ============
    @GetMapping("/point/info")
    public Result<?> pointInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        MemberPoint mp = memberPointService.getById(userId);
        if (mp == null) {
            mp = new MemberPoint();
            mp.setUserId(userId); mp.setTotalPoint(0); mp.setAvailablePoint(0); mp.setFreezePoint(0);
            memberPointService.save(mp);
        }
        return Result.success(mp);
    }

    @GetMapping("/point/log")
    public Result<?> pointLog(HttpServletRequest request, PageQuery pageQuery) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(pointLogService.page(pageQuery.toPage(),
                new LambdaQueryWrapper<PointLog>().eq(PointLog::getUserId, userId).orderByDesc(PointLog::getCreateTime)));
    }

    // ============ 足迹 ============
    @PostMapping("/footprint/add")
    public Result<?> addFootprint(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = null;
        try { userId = (Long) request.getAttribute("userId"); } catch (Exception ignored) {}
        if (userId == null) return Result.success();
        Long productId = Long.valueOf(body.get("productId").toString());
        UserFootprint fp = new UserFootprint();
        fp.setUserId(userId); fp.setProductId(productId); fp.setBrowseTime(java.time.LocalDateTime.now());
        footprintService.save(fp);
        return Result.success();
    }

    @GetMapping("/footprint/list")
    public Result<?> footprintList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(footprintService.list(new LambdaQueryWrapper<UserFootprint>()
                .eq(UserFootprint::getUserId, userId).orderByDesc(UserFootprint::getBrowseTime).last("LIMIT 50")));
    }

    @DeleteMapping("/footprint/delete/{id}")
    public Result<?> deleteFootprint(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        footprintService.remove(new LambdaQueryWrapper<UserFootprint>()
                .eq(UserFootprint::getId, id).eq(UserFootprint::getUserId, userId));
        return Result.success();
    }

    // ============ 优惠券 ============
    @GetMapping("/coupon/available")
    public Result<?> availableCoupons(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        // 查询所有启用的优惠券（不过滤已领取的）
        List<Coupon> allCoupons = couponService.list(new LambdaQueryWrapper<Coupon>()
                .eq(Coupon::getStatus, 1)
                .orderByDesc(Coupon::getCreateTime));
        List<Map<String, Object>> available = new java.util.ArrayList<>();
        for (Coupon c : allCoupons) {
            Map<String, Object> item = new java.util.HashMap<>();
            item.put("id", c.getId());
            item.put("name", c.getName());
            item.put("type", c.getType());
            item.put("faceValue", c.getFaceValue());
            item.put("minAmount", c.getMinAmount());
            item.put("totalCount", c.getTotalCount());
            item.put("takenCount", c.getTakenCount());
            item.put("startTime", c.getStartTime());
            item.put("endTime", c.getEndTime());
            // 检查用户是否已领取
            long userTaken = userCouponService.count(new LambdaQueryWrapper<UserCoupon>()
                    .eq(UserCoupon::getUserId, userId).eq(UserCoupon::getCouponId, c.getId()));
            item.put("hasTaken", userTaken > 0 ? 1 : 0);
            available.add(item);
        }
        return Result.success(available);
    }

    @PostMapping("/coupon/take/{couponId}")
    public Result<?> takeCoupon(@PathVariable Long couponId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Coupon c = couponService.getById(couponId);
        if (c == null || c.getStatus() != 1) return Result.error(404, "优惠券不存在");
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        if (c.getStartTime() != null && c.getStartTime().isAfter(now)) return Result.error(400, "优惠券未开始");
        if (c.getEndTime() != null && c.getEndTime().isBefore(now)) return Result.error(400, "优惠券已过期");
        if (c.getTakenCount() >= c.getTotalCount()) return Result.error(400, "已领完");
        long existCount = userCouponService.count(new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getUserId, userId).eq(UserCoupon::getCouponId, couponId));
        if (existCount > 0) return Result.error(400, "已领取过");
        UserCoupon uc = new UserCoupon();
        uc.setUserId(userId); uc.setCouponId(couponId); uc.setStatus("unused");
        uc.setTakeTime(now);
        userCouponService.save(uc);
        c.setTakenCount(c.getTakenCount() + 1); couponService.updateById(c);
        return Result.success("领取成功", null);
    }

    @GetMapping("/coupon/my")
    public Result<?> myCoupons(HttpServletRequest request, @RequestParam(required = false) String status) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<UserCoupon> qw = new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getUserId, userId);
        if (status != null && !status.isEmpty()) qw.eq(UserCoupon::getStatus, status);
        qw.orderByDesc(UserCoupon::getTakeTime);
        List<UserCoupon> coupons = userCouponService.list(qw);
        // 关联优惠券详情
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (UserCoupon uc : coupons) {
            Coupon c = couponService.getById(uc.getCouponId());
            Map<String, Object> item = new java.util.HashMap<>();
            item.put("id", uc.getId());
            item.put("couponId", uc.getCouponId());
            item.put("status", uc.getStatus());
            item.put("takeTime", uc.getTakeTime());
            item.put("useTime", uc.getUseTime());
            if (c != null) {
                item.put("name", c.getName());
                item.put("type", c.getType());
                item.put("faceValue", c.getFaceValue());
                item.put("minAmount", c.getMinAmount());
                item.put("startTime", c.getStartTime());
                item.put("endTime", c.getEndTime());
            }
            result.add(item);
        }
        return Result.success(result);
    }

}
