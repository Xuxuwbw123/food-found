package com.freshtrace.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.entity.*;
import com.freshtrace.unified.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/admin/stock")
public class SmartStockController {

    @Autowired private StockAlertService alertService;
    @Autowired private SalesStatisticsService statsService;
    @Autowired private ProductService productService;
    @Autowired private SysNoticeService noticeService;

    // ============ 库存预警配置 ============
    @GetMapping("/alerts")
    public Result<?> alertList() {
        return Result.success(alertService.list());
    }

    @PostMapping("/alerts")
    public Result<?> addAlert(@RequestBody StockAlert alert) {
        alert.setCreateTime(LocalDateTime.now());
        alertService.save(alert);
        return Result.success();
    }

    @DeleteMapping("/alerts/{id}")
    public Result<?> deleteAlert(@PathVariable Long id) {
        alertService.removeById(id);
        return Result.success();
    }

    // ============ 库存检查（手动触发） ============
    @PostMapping("/check")
    public Result<?> checkStock() {
        List<StockAlert> alerts = alertService.list();
        List<Map<String, Object>> warnings = new ArrayList<>();
        for (StockAlert alert : alerts) {
            Product p = productService.getById(alert.getProductId());
            if (p != null && p.getStock() <= alert.getAlertQuantity()) {
                Map<String, Object> w = new HashMap<>();
                w.put("productId", p.getId());
                w.put("productName", p.getProductName());
                w.put("currentStock", p.getStock());
                w.put("alertQuantity", alert.getAlertQuantity());
                w.put("farmerId", p.getFarmerId());
                warnings.add(w);

                // 通知农户
                if (p.getFarmerId() != null) {
                    try {
                        SysNotice notice = new SysNotice();
                        notice.setUserId(p.getFarmerId()); notice.setNoticeType("stock");
                        notice.setTitle("库存预警");
                        notice.setContent("【" + p.getProductName() + "】库存不足" + p.getStock() + "，请及时补货");
                        notice.setIsRead(0); notice.setCreateTime(LocalDateTime.now());
                        noticeService.save(notice);
                        alert.setLastNotified(LocalDateTime.now());
                        alertService.updateById(alert);
                    } catch (Exception ignored) {}
                }
            }
        }
        return Result.success(warnings);
    }

    // ============ 销量统计 ============
    @GetMapping("/sales/{farmerId}")
    public Result<?> salesStats(@PathVariable Long farmerId,
            @RequestParam(defaultValue = "7") Integer days) {
        LocalDate from = LocalDate.now().minusDays(days);
        List<SalesStatistics> stats = statsService.list(new LambdaQueryWrapper<SalesStatistics>()
                .eq(SalesStatistics::getFarmerId, farmerId)
                .ge(SalesStatistics::getStatDate, from)
                .orderByAsc(SalesStatistics::getStatDate));
        return Result.success(stats);
    }
}
