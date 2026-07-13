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

    // ============ 会员卡余额支付 ============
    @PostMapping("/api/order/pay-by-balance")
    @Transactional
    public Result<?> payByBalance(@RequestBody Map<String, Object> body) {
        Long userId = UserContext.getUserId();
        Long orderId = Long.valueOf(body.get("orderId").toString());

        OrderInfo order = orderInfoService.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return Result.error(404, "订单不存在");
        }
        if (order.getOrderStatus() != 0) {
            return Result.error(400, "订单状态不允许支付");
        }

        SysUser user = userMapper.selectById(userId);
        if (user == null) return Result.error(404, "用户不存在");

        // 会员折扣和优惠券已在下单时扣除，直接用payAmount
        BigDecimal payAmount = order.getPayAmount() != null ? order.getPayAmount() : order.getTotalAmount();

        // 检查余额
        BigDecimal balance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        if (balance.compareTo(payAmount) < 0) {
            return Result.error(400, "余额不足，当前余额 ¥" + balance + "，需支付 ¥" + payAmount);
        }

        // 扣除余额
        user.setBalance(balance.subtract(payAmount));
        userMapper.updateById(user);

        // 更新订单状态（使用mapper直接更新）
        OrderInfo updateOrder = new OrderInfo();
        updateOrder.setId(orderId);
        updateOrder.setOrderStatus(1);
        updateOrder.setPayType(3); // 3=会员卡
        updateOrder.setPayTime(LocalDateTime.now());
        int rows = orderInfoMapper.updateById(updateOrder);
        if (rows == 0) {
            throw new RuntimeException("支付失败，订单状态已变更");
        }

        // 更新支付记录
        PaymentInfo pay = paymentInfoService.getOne(new LambdaQueryWrapper<PaymentInfo>()
                .eq(PaymentInfo::getOrderId, orderId));
        if (pay != null) {
            pay.setPayStatus(1);
            pay.setPayTime(LocalDateTime.now());
            pay.setPayType(3);
            paymentInfoService.updateById(pay);
        }

        // 记录订单日志
        OrderLog log = new OrderLog();
        log.setOrderId(orderId);
        log.setOrderNo(order.getOrderNo());
        log.setOrderStatus(1);
        log.setOperatorType(1);
        log.setOperatorId(userId);
        log.setRemark("会员卡余额支付 ¥" + payAmount);
        log.setCreateTime(LocalDateTime.now());
        orderLogService.save(log);

        // 累计消费 + 自动升级
        BigDecimal totalSpent = user.getTotalSpent() != null ? user.getTotalSpent() : BigDecimal.ZERO;
        BigDecimal newTotalSpent = totalSpent.add(payAmount);
        user.setTotalSpent(newTotalSpent);
        userMapper.updateById(user);
        checkAndUpgradeMember(userId, newTotalSpent);

        // 发放积分
        awardPoints(userId, payAmount, user.getMemberLevel() != null ? user.getMemberLevel() : 0);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("payAmount", payAmount);
        result.put("balance", user.getBalance());
        return Result.success("支付成功", result);
    }

    // ============ 获取支付选项（含会员折扣信息） ============
    @GetMapping("/api/order/pay-options/{orderId}")
    public Result<?> getPayOptions(@PathVariable Long orderId) {
        Long userId = UserContext.getUserId();
        OrderInfo order = orderInfoService.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return Result.error(404, "订单不存在");
        }

        SysUser user = userMapper.selectById(userId);
        if (user == null) return Result.error(404, "用户不存在");

        BigDecimal balance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        int memberLevel = user.getMemberLevel() != null ? user.getMemberLevel() : 0;
        String levelName = "普通用户";
        BigDecimal discountRate = BigDecimal.ONE;

        if (memberLevel > 0) {
            MemberLevelConfig config = levelConfigService.getOne(new LambdaQueryWrapper<MemberLevelConfig>()
                    .eq(MemberLevelConfig::getLevel, memberLevel));
            if (config != null) {
                levelName = config.getLevelName();
                discountRate = config.getDiscountRate() != null ? config.getDiscountRate() : BigDecimal.ONE;
            }
        }

        // 会员折扣和优惠券已在下单时扣除，直接用payAmount
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

    private void checkAndUpgradeMember(Long userId, BigDecimal totalSpent) {
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

    private void awardPoints(Long userId, BigDecimal amount, int memberLevel) {
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
        log.setRemark("消费获得积分（" + pointsRate + "倍）");
        log.setCreateTime(LocalDateTime.now());
        pointLogService.save(log);
    }
}
