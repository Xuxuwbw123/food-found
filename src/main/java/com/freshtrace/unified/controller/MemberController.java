package com.freshtrace.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshtrace.unified.common.PageQuery;
import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.common.UserContext;
import com.freshtrace.unified.entity.*;
import com.freshtrace.unified.mapper.SysUserMapper;
import com.freshtrace.unified.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
public class MemberController {

    @Autowired private MemberLevelConfigService levelConfigService;
    @Autowired private PointsExchangeRuleService exchangeRuleService;
    @Autowired private RechargeRecordService rechargeService;
    @Autowired private SysUserMapper userMapper;
    @Autowired private MemberPointService memberPointService;
    @Autowired private UserCouponService userCouponService;
    @Autowired private SysNoticeService noticeService;
    @Autowired private CouponService couponService;

    // ============ 会员等级配置（管理员） ============
    @GetMapping({"/admin/member-level/config", "/api/admin/member-level/config"})
    public Result<?> getLevelConfig() {
        return Result.success(levelConfigService.list(new LambdaQueryWrapper<MemberLevelConfig>()
                .orderByAsc(MemberLevelConfig::getLevel)));
    }

    @PutMapping({"/admin/member-level/config", "/api/admin/member-level/config"})
    public Result<?> updateLevelConfig(@RequestBody List<Map<String, Object>> configs) {
        for (Map<String, Object> c : configs) {
            Long id = Long.valueOf(c.get("id").toString());
            MemberLevelConfig config = new MemberLevelConfig();
            config.setId(id);
            if (c.containsKey("levelName")) config.setLevelName((String) c.get("levelName"));
            if (c.containsKey("discountRate")) config.setDiscountRate(new BigDecimal(c.get("discountRate").toString()));
            if (c.containsKey("upgradeAmount")) config.setUpgradeAmount(new BigDecimal(c.get("upgradeAmount").toString()));
            if (c.containsKey("rechargeMin")) config.setRechargeMin(new BigDecimal(c.get("rechargeMin").toString()));
            if (c.containsKey("pointsRate")) config.setPointsRate(Integer.parseInt(c.get("pointsRate").toString()));
            levelConfigService.updateById(config);
        }
        return Result.success("更新成功", null);
    }

    // ============ 管理员手动调整会员等级 ============
    @PutMapping({"/admin/member-level/adjust/{userId}", "/api/admin/member-level/adjust/{userId}"})
    public Result<?> adjustMemberLevel(@PathVariable Long userId, @RequestBody Map<String, Object> body) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) return Result.error(404, "用户不存在");
        int newLevel = Integer.parseInt(body.get("memberLevel").toString());
        if (newLevel < 0 || newLevel > 3) return Result.error(400, "等级范围0-3");
        user.setMemberLevel(newLevel);
        userMapper.updateById(user);
        // 发送通知
        try {
            MemberLevelConfig config = levelConfigService.getOne(new LambdaQueryWrapper<MemberLevelConfig>()
                    .eq(MemberLevelConfig::getLevel, newLevel));
            SysNotice notice = new SysNotice();
            notice.setUserId(userId); notice.setNoticeType("member");
            notice.setTitle("会员等级变更");
            notice.setContent("您的会员等级已调整为" + (config != null ? config.getLevelName() : "普通用户"));
            notice.setIsRead(0); notice.setCreateTime(LocalDateTime.now());
            noticeService.save(notice);
        } catch (Exception ignored) {}
        return Result.success("调整成功", null);
    }

    // ============ 积分兑换规则（管理员） ============
    @GetMapping({"/admin/points-exchange/rules", "/api/admin/points-exchange/rules"})
    public Result<?> getExchangeRules() {
        return Result.success(exchangeRuleService.list(new LambdaQueryWrapper<PointsExchangeRule>()
                .orderByDesc(PointsExchangeRule::getCreateTime)));
    }

    @PostMapping({"/admin/points-exchange/rules", "/api/admin/points-exchange/rules"})
    public Result<?> addExchangeRule(@RequestBody PointsExchangeRule rule) {
        rule.setCreateTime(LocalDateTime.now());
        exchangeRuleService.save(rule);
        return Result.success("创建成功", null);
    }

    @PutMapping({"/admin/points-exchange/rules", "/api/admin/points-exchange/rules"})
    public Result<?> updateExchangeRule(@RequestBody PointsExchangeRule rule) {
        exchangeRuleService.updateById(rule);
        return Result.success();
    }

    @DeleteMapping({"/admin/points-exchange/rules/{id}", "/api/admin/points-exchange/rules/{id}"})
    public Result<?> deleteExchangeRule(@PathVariable Long id) {
        exchangeRuleService.removeById(id);
        return Result.success();
    }

    // ============ 用户端：获取会员信息 ============
    @GetMapping("/api/member/info")
    public Result<?> getMemberInfo() {
        Long userId = UserContext.getUserId();
        SysUser user = userMapper.selectById(userId);
        if (user == null) return Result.error(404, "用户不存在");

        MemberLevelConfig config = levelConfigService.getOne(new LambdaQueryWrapper<MemberLevelConfig>()
                .eq(MemberLevelConfig::getLevel, user.getMemberLevel() != null ? user.getMemberLevel() : 0));

        Map<String, Object> data = new HashMap<>();
        data.put("memberLevel", user.getMemberLevel() != null ? user.getMemberLevel() : 0);
        data.put("levelName", config != null ? config.getLevelName() : "普通用户");
        data.put("discountRate", config != null ? config.getDiscountRate() : BigDecimal.ONE);
        data.put("totalSpent", user.getTotalSpent() != null ? user.getTotalSpent() : BigDecimal.ZERO);
        data.put("balance", user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO);

        // 获取积分
        MemberPoint mp = memberPointService.getById(userId);
        data.put("points", mp != null ? mp.getAvailablePoint() : 0);

        // 获取升级门槛
        List<MemberLevelConfig> allLevels = levelConfigService.list(new LambdaQueryWrapper<MemberLevelConfig>()
                .orderByAsc(MemberLevelConfig::getLevel));
        data.put("levels", allLevels);

        return Result.success(data);
    }

    // ============ 用户端：充值 ============
    @PostMapping("/api/member/recharge")
    public Result<?> recharge(@RequestBody Map<String, Object> body) {
        Long userId = UserContext.getUserId();
        BigDecimal amount = new BigDecimal(body.get("amount").toString());

        SysUser user = userMapper.selectById(userId);
        if (user == null) return Result.error(404, "用户不存在");

        // 检查最低充值金额
        MemberLevelConfig targetLevel = levelConfigService.getOne(new LambdaQueryWrapper<MemberLevelConfig>()
                .eq(MemberLevelConfig::getLevel, 1)); // 普通会员
        if (targetLevel != null && amount.compareTo(targetLevel.getRechargeMin()) < 0) {
            return Result.error(400, "最低充值金额为 ¥" + targetLevel.getRechargeMin());
        }

        BigDecimal balanceBefore = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        BigDecimal balanceAfter = balanceBefore.add(amount);

        // 更新余额
        user.setBalance(balanceAfter);
        userMapper.updateById(user);

        // 记录充值
        RechargeRecord record = new RechargeRecord();
        record.setUserId(userId); record.setAmount(amount);
        record.setBalanceBefore(balanceBefore); record.setBalanceAfter(balanceAfter);
        record.setPayType(1); record.setStatus(1); record.setCreateTime(LocalDateTime.now());
        rechargeService.save(record);

        // 检查是否自动升级
        checkAndUpgradeMember(userId, balanceAfter, null);

        return Result.success("充值成功", Map.of("balance", balanceAfter));
    }

    // ============ 用户端：积分兑换 ============
    @PostMapping("/api/points/exchange")
    public Result<?> exchangePoints(@RequestBody Map<String, Object> body) {
        Long userId = UserContext.getUserId();
        Long ruleId = Long.valueOf(body.get("ruleId").toString());

        PointsExchangeRule rule = exchangeRuleService.getById(ruleId);
        if (rule == null || rule.getStatus() != 1) return Result.error(404, "兑换规则不存在");

        MemberPoint mp = memberPointService.getById(userId);
        if (mp == null || mp.getAvailablePoint() < rule.getPointsCost()) {
            return Result.error(400, "积分不足");
        }

        // 扣除积分
        mp.setAvailablePoint(mp.getAvailablePoint() - rule.getPointsCost());
        memberPointService.updateById(mp);

        // 发放优惠券
        if (rule.getCouponId() != null) {
            UserCoupon uc = new UserCoupon();
            uc.setUserId(userId); uc.setCouponId(rule.getCouponId());
            uc.setStatus("unused"); uc.setTakeTime(LocalDateTime.now());
            userCouponService.save(uc);
        }

        return Result.success("兑换成功", null);
    }

    // ============ 用户端：获取可用兑换规则 ============
    @GetMapping("/api/points/exchange/rules")
    public Result<?> getExchangeRulesForUser() {
        return Result.success(exchangeRuleService.list(new LambdaQueryWrapper<PointsExchangeRule>()
                .eq(PointsExchangeRule::getStatus, 1)));
    }

    // ============ 内部方法：检查并升级会员等级 ============
    public void checkAndUpgradeMember(Long userId, BigDecimal totalSpent, BigDecimal orderAmount) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) return;

        BigDecimal spent = totalSpent != null ? totalSpent :
                (user.getTotalSpent() != null ? user.getTotalSpent() : BigDecimal.ZERO).add(orderAmount != null ? orderAmount : BigDecimal.ZERO);

        // 更新累计消费
        user.setTotalSpent(spent);

        // 查找应该升级到的等级
        List<MemberLevelConfig> levels = levelConfigService.list(new LambdaQueryWrapper<MemberLevelConfig>()
                .orderByDesc(MemberLevelConfig::getUpgradeAmount));
        int newLevel = 0;
        for (MemberLevelConfig level : levels) {
            if (level.getUpgradeAmount().compareTo(BigDecimal.ZERO) > 0 && spent.compareTo(level.getUpgradeAmount()) >= 0) {
                newLevel = level.getLevel();
                break;
            }
        }

        Integer currentLevel = user.getMemberLevel() != null ? user.getMemberLevel() : 0;
        if (newLevel > currentLevel) {
            user.setMemberLevel(newLevel);
            // 发送升级通知
            MemberLevelConfig newConfig = levelConfigService.getOne(new LambdaQueryWrapper<MemberLevelConfig>()
                    .eq(MemberLevelConfig::getLevel, newLevel));
            if (newConfig != null) {
                try {
                    SysNotice notice = new SysNotice();
                    notice.setUserId(userId); notice.setNoticeType("member");
                    notice.setTitle("会员升级通知");
                    notice.setContent("恭喜您升级为" + newConfig.getLevelName() + "，享受" + newConfig.getDiscountRate().multiply(new BigDecimal("10")).stripTrailingZeros().toPlainString() + "折优惠！");
                    notice.setIsRead(0); notice.setCreateTime(LocalDateTime.now());
                    noticeService.save(notice);
                } catch (Exception ignored) {}
            }
        }
        userMapper.updateById(user);
    }

    // ============ 内部方法：获取会员折扣率 ============
    public BigDecimal getMemberDiscountRate(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null || user.getMemberLevel() == null || user.getMemberLevel() == 0) return BigDecimal.ONE;
        MemberLevelConfig config = levelConfigService.getOne(new LambdaQueryWrapper<MemberLevelConfig>()
                .eq(MemberLevelConfig::getLevel, user.getMemberLevel()));
        return config != null ? config.getDiscountRate() : BigDecimal.ONE;
    }

    // ============ 内部方法：获取积分倍率 ============
    public int getPointsRate(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null || user.getMemberLevel() == null || user.getMemberLevel() == 0) return 1;
        MemberLevelConfig config = levelConfigService.getOne(new LambdaQueryWrapper<MemberLevelConfig>()
                .eq(MemberLevelConfig::getLevel, user.getMemberLevel()));
        return config != null && config.getPointsRate() != null ? config.getPointsRate() : 1;
    }
}
