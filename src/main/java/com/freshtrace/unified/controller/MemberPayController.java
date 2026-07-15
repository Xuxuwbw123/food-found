package com.freshtrace.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.common.UserContext;
import com.freshtrace.unified.entity.*;
import com.freshtrace.unified.mapper.OrderInfoMapper;
import com.freshtrace.unified.mapper.SysUserMapper;
import com.freshtrace.unified.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@RestController
public class MemberPayController {

    @Autowired private OrderInfoService orderInfoService;
    @Autowired private OrderInfoMapper orderInfoMapper;
    @Autowired private OrderLogService orderLogService;
    @Autowired private PaymentInfoService paymentInfoService;
    @Autowired private SysUserMapper userMapper;
    @Autowired private MemberLevelConfigService levelConfigService;
    @Autowired private MemberPointService memberPointService;
    @Autowired private PointLogService pointLogService;

    // ============ 浼氬憳鍗′綑棰濇敮浠?============
    @PostMapping("/api/order/pay-by-balance")
    @Transactional
    public Result<?> payByBalance(@RequestBody Map<String, Object> body) {
        Long userId = UserContext.getUserId();
        Long orderId = Long.valueOf(body.get("orderId").toString());

        OrderInfo order = orderInfoService.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return Result.error(404, "璁㈠崟涓嶅瓨鍦?);
        }
        if (order.getOrderStatus() != 0) {
            return Result.error(400, "璁㈠崟鐘舵€佷笉鍏佽鏀粯");
        }

        SysUser user = userMapper.selectById(userId);
        if (user == null) return Result.error(404, "鐢ㄦ埛涓嶅瓨鍦?);

        // 浼氬憳鎶樻墸鍜屼紭鎯犲埜宸插湪涓嬪崟鏃舵墸闄わ紝鐩存帴鐢╬ayAmount
        BigDecimal payAmount = order.getPayAmount() != null ? order.getPayAmount() : order.getTotalAmount();

        // 妫€鏌ヤ綑棰?
        BigDecimal balance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        if (balance.compareTo(payAmount) < 0) {
            return Result.error(400, "浣欓涓嶈冻锛屽綋鍓嶄綑棰?楼" + balance + "锛岄渶鏀粯 楼" + payAmount);
        }

        // 鎵ｉ櫎浣欓
        user.setBalance(balance.subtract(payAmount));
        userMapper.updateById(user);

        // 鏇存柊璁㈠崟鐘舵€侊紙浣跨敤mapper鐩存帴鏇存柊锛?
        OrderInfo updateOrder = new OrderInfo();
        updateOrder.setId(orderId);
        updateOrder.setOrderStatus(1);
        updateOrder.setPayType(3); // 3=浼氬憳鍗?
        updateOrder.setPayTime(LocalDateTime.now());
        int rows = orderInfoMapper.updateById(updateOrder);
        if (rows == 0) {
            throw new RuntimeException("鏀粯澶辫触锛岃鍗曠姸鎬佸凡鍙樻洿");
        }

        // 鏇存柊鏀粯璁板綍
        PaymentInfo pay = paymentInfoService.getOne(new LambdaQueryWrapper<PaymentInfo>()
                .eq(PaymentInfo::getOrderId, orderId));
        if (pay != null) {
            pay.setPayStatus(1);
            pay.setPayTime(LocalDateTime.now());
            pay.setPayType(3);
            paymentInfoService.updateById(pay);
        }

        // 璁板綍璁㈠崟鏃ュ織
        OrderLog log = new OrderLog();
        log.setOrderId(orderId);
        log.setOrderNo(order.getOrderNo());
        log.setOrderStatus(1);
        log.setOperatorType(1);
        log.setOperatorId(userId);
        log.setRemark("浼氬憳鍗′綑棰濇敮浠?楼" + payAmount);
        log.setCreateTime(LocalDateTime.now());
        orderLogService.save(log);

        // 绱娑堣垂 + 鑷姩鍗囩骇
        BigDecimal totalSpent = user.getTotalSpent() != null ? user.getTotalSpent() : BigDecimal.ZERO;
        BigDecimal newTotalSpent = totalSpent.add(payAmount);
        user.setTotalSpent(newTotalSpent);
        userMapper.updateById(user);
        checkAndUpgradeMember(userId, newTotalSpent);

        // 鍙戞斁绉垎
        awardPoints(userId, payAmount, user.getMemberLevel() != null ? user.getMemberLevel() : 0);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("payAmount", payAmount);
        result.put("balance", user.getBalance());
        return Result.success("鏀粯鎴愬姛", result);
    }

    // ============ 鑾峰彇鏀粯閫夐」锛堝惈浼氬憳鎶樻墸淇℃伅锛?============
    @GetMapping("/api/order/pay-options/{orderId}")
    public Result<?> getPayOptions(@PathVariable Long orderId) {
        Long userId = UserContext.getUserId();
        OrderInfo order = orderInfoService.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return Result.error(404, "璁㈠崟涓嶅瓨鍦?);
        }

        SysUser user = userMapper.selectById(userId);
        if (user == null) return Result.error(404, "鐢ㄦ埛涓嶅瓨鍦?);

        BigDecimal balance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        int memberLevel = user.getMemberLevel() != null ? user.getMemberLevel() : 0;
        String levelName = "鏅€氱敤鎴?;
        BigDecimal discountRate = BigDecimal.ONE;

        if (memberLevel > 0) {
            MemberLevelConfig config = levelConfigService.getOne(new LambdaQueryWrapper<MemberLevelConfig>()
                    .eq(MemberLevelConfig::getLevel, memberLevel));
            if (config != null) {
                levelName = config.getLevelName();
                discountRate = config.getDiscountRate() != null ? config.getDiscountRate() : BigDecimal.ONE;
            }
        }

        // 浼氬憳鎶樻墸鍜屼紭鎯犲埜宸插湪涓嬪崟鏃舵墸闄わ紝鐩存帴鐢╬ayAmount
        BigDecimal originalAmount = order.getTotalAmount();
        BigDecimal payAmount = order.getPayAmount() != null ? order.getPayAmount() : order.getTotalAmount();
        BigDecimal totalDiscount = order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO;
        boolean canPayByBalance = balance.compareTo(payAmount) >= 0;

        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("originalAmount", originalAmount);
        data.put("payAmount", payAmount);
        data.put("totalDiscount", totalDiscount);
        data.put("balance", balance);
        data.put("memberLevel", memberLevel);
        data.put("levelName", levelName);
        data.put("discountRate", discountRate);
        data.put("canPayByBalance", canPayByBalance);
        data.put("savings", totalDiscount);
        return Result.success(data);
    }

    private BigDecimal getMemberDiscountRate(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null || user.getMemberLevel() == null || user.getMemberLevel() == 0) return BigDecimal.ONE;
        MemberLevelConfig config = levelConfigService.getOne(new LambdaQueryWrapper<MemberLevelConfig>()
                .eq(MemberLevelConfig::getLevel, user.getMemberLevel()));
        return config != null && config.getDiscountRate() != null ? config.getDiscountRate() : BigDecimal.ONE;
    }

    @Transactional
    public void checkAndUpgradeMember(Long userId, BigDecimal totalSpent) {
        List<MemberLevelConfig> levels = levelConfigService.list(new LambdaQueryWrapper<MemberLevelConfig>()
                .orderByDesc(MemberLevelConfig::getUpgradeAmount));
        int newLevel = 0;
        for (MemberLevelConfig level : levels) {
            if (level.getUpgradeAmount().compareTo(BigDecimal.ZERO) > 0
                    && totalSpent.compareTo(level.getUpgradeAmount()) >= 0) {
                newLevel = level.getLevel();
                break;
            }
        }
        SysUser user = userMapper.selectById(userId);
        Integer currentLevel = user.getMemberLevel() != null ? user.getMemberLevel() : 0;
        if (newLevel > currentLevel) {
            user.setMemberLevel(newLevel);
            userMapper.updateById(user);
        }
    }

    @Transactional
    public void awardPoints(Long userId, BigDecimal amount, int memberLevel) {
        int pointsRate = 1;
        if (memberLevel > 0) {
            MemberLevelConfig config = levelConfigService.getOne(new LambdaQueryWrapper<MemberLevelConfig>()
                    .eq(MemberLevelConfig::getLevel, memberLevel));
            if (config != null && config.getPointsRate() != null) pointsRate = config.getPointsRate();
        }
        int points = amount.intValue() * pointsRate;
        if (points <= 0) return;

        MemberPoint mp = memberPointService.getById(userId);
        if (mp == null) {
            mp = new MemberPoint();
            mp.setUserId(userId); mp.setTotalPoint(0); mp.setAvailablePoint(0); mp.setFreezePoint(0);
            memberPointService.save(mp);
        }
        mp.setAvailablePoint(mp.getAvailablePoint() + points);
        mp.setTotalPoint(mp.getTotalPoint() + points);
        memberPointService.updateById(mp);

        PointLog log = new PointLog();
        log.setUserId(userId); log.setType("purchase");
        log.setPoint(points); log.setBalance(mp.getAvailablePoint());
        log.setRemark("娑堣垂鑾峰緱绉垎锛? + pointsRate + "鍊嶏級");
        log.setCreateTime(LocalDateTime.now());
        pointLogService.save(log);
    }
}
