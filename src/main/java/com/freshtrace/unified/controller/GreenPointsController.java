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
public class GreenPointsController {

    @Autowired private GreenPointsRuleService ruleService;
    @Autowired private MemberPointService pointService;
    @Autowired private PointLogService pointLogService;
    @Autowired private CharityCertService certService;

    // ============ 管理员：绿色积分规则 ============
    @GetMapping("/admin/green-points/rules")
    public Result<?> ruleList() {
        return Result.success(ruleService.list());
    }

    @PostMapping("/admin/green-points/rules")
    public Result<?> ruleAdd(@RequestBody GreenPointsRule rule) {
        rule.setCreateTime(LocalDateTime.now());
        ruleService.save(rule);
        return Result.success();
    }

    @PutMapping("/admin/green-points/rules")
    public Result<?> ruleUpdate(@RequestBody GreenPointsRule rule) {
        ruleService.updateById(rule);
        return Result.success();
    }

    @DeleteMapping("/admin/green-points/rules/{id}")
    public Result<?> ruleDelete(@PathVariable Long id) {
        ruleService.removeById(id);
        return Result.success();
    }

    // ============ 用户端：获取绿色积分规则 ============
    @GetMapping("/api/green-points/rules")
    public Result<?> userRules() {
        return Result.success(ruleService.list(new LambdaQueryWrapper<GreenPointsRule>()
                .eq(GreenPointsRule::getStatus, 1)));
    }

    // ============ 用户端：获取助农证书 ============
    @GetMapping("/api/green-points/certs")
    public Result<?> myCerts() {
        Long userId = UserContext.getUserId();
        return Result.success(certService.list(new LambdaQueryWrapper<CharityCert>()
                .eq(CharityCert::getUserId, userId).orderByDesc(CharityCert::getCreateTime)));
    }

    // ============ 内部方法：发放绿色积分 ============
    public void awardGreenPoints(Long userId, String actionType) {
        GreenPointsRule rule = ruleService.getOne(new LambdaQueryWrapper<GreenPointsRule>()
                .eq(GreenPointsRule::getActionType, actionType).eq(GreenPointsRule::getStatus, 1));
        if (rule == null) return;

        MemberPoint mp = pointService.getById(userId);
        if (mp == null) {
            mp = new MemberPoint();
            mp.setUserId(userId); mp.setTotalPoint(0); mp.setAvailablePoint(0); mp.setFreezePoint(0);
            pointService.save(mp);
        }
        mp.setAvailablePoint(mp.getAvailablePoint() + rule.getPoints());
        mp.setTotalPoint(mp.getTotalPoint() + rule.getPoints());
        pointService.updateById(mp);

        PointLog log = new PointLog();
        log.setUserId(userId); log.setType("green");
        log.setPoint(rule.getPoints()); log.setBalance(mp.getAvailablePoint());
        log.setRemark(rule.getRuleName());
        log.setCreateTime(LocalDateTime.now());
        pointLogService.save(log);
    }
}
