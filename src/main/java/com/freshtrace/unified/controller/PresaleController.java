package com.freshtrace.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshtrace.unified.common.PageQuery;
import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.common.UserContext;
import com.freshtrace.unified.entity.*;
import com.freshtrace.unified.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
public class PresaleController {

    @Autowired private ProductService productService;
    @Autowired private OrderInfoService orderInfoService;
    @Autowired private GrowthTimelineService growthService;
    @Autowired private TraceabilityService traceService;
    @Autowired private SysNoticeService noticeService;

    // ============ 用户端：预售专区 ============
    @GetMapping("/api/presale/list")
    public Result<?> presaleList(PageQuery pageQuery) {
        Page<Product> page = productService.page(pageQuery.toPage(), new LambdaQueryWrapper<Product>()
                .eq(Product::getIsPresale, 1)
                .eq(Product::getStatus, 1)
                .eq(Product::getAuditStatus, 1)
                .orderByDesc(Product::getCreateTime));
        return Result.success(page);
    }

    // ============ 用户端：获取预售订单的生长动态 ============
    @GetMapping("/api/presale/growth/{orderId}")
    public Result<?> getPresaleGrowth(@PathVariable Long orderId) {
        Long userId = UserContext.getUserId();
        OrderInfo order = orderInfoService.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return Result.error(403, "无权查看");
        }
        if (order.getTraceId() == null) {
            return Result.error(400, "该订单不是预售订单");
        }

        List<GrowthTimeline> growthList = growthService.list(new LambdaQueryWrapper<GrowthTimeline>()
                .eq(GrowthTimeline::getTraceId, order.getTraceId())
                .orderByAsc(GrowthTimeline::getCreateTime));

        Traceability trace = traceService.getById(order.getTraceId());

        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("trace", trace);
        data.put("growthList", growthList);
        data.put("presaleStatus", order.getPresaleStatus());
        return Result.success(data);
    }

    // ============ 农户端：发送生长动态 ============
    @PostMapping("/api/farmer/presale/growth")
    public Result<?> sendGrowthUpdate(@RequestBody Map<String, Object> body) {
        Long userId = UserContext.getUserId();
        Long traceId = Long.valueOf(body.get("traceId").toString());
        String title = (String) body.get("title");
        String stage = (String) body.get("stage");
        String content = (String) body.get("content");
        String imageUrl = (String) body.get("imageUrl");

        Traceability trace = traceService.getById(traceId);
        if (trace == null) return Result.error(404, "溯源批次不存在");

        GrowthTimeline record = new GrowthTimeline();
        record.setTraceId(traceId);
        record.setTitle(title);
        record.setStage(stage);
        record.setContent(content);
        record.setImageUrls(imageUrl);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        growthService.save(record);

        // 更新关联的预售订单状态
        int presaleStatus = stageToPresaleStatus(stage);
        if (presaleStatus > 0) {
            List<OrderInfo> orders = orderInfoService.list(new LambdaQueryWrapper<OrderInfo>()
                    .eq(OrderInfo::getTraceId, traceId)
                    .in(OrderInfo::getPresaleStatus, 1, 2));
            for (OrderInfo order : orders) {
                if (order.getPresaleStatus() < presaleStatus) {
                    order.setPresaleStatus(presaleStatus);
                    orderInfoService.updateById(order);
                    try {
                        SysNotice notice = new SysNotice();
                        notice.setUserId(order.getUserId());
                        notice.setNoticeType("presale");
                        notice.setTitle("预售商品动态更新");
                        notice.setContent("您购买的「" + trace.getProductName() + "」已进入" + stageName(stage) + "阶段，快来看看吧！");
                        notice.setIsRead(0);
                        notice.setCreateTime(LocalDateTime.now());
                        noticeService.save(notice);
                    } catch (Exception ignored) {}
                }
            }
        }

        return Result.success("动态发送成功", null);
    }

    // ============ 农户端：获取我的预售批次列表 ============
    @GetMapping("/api/farmer/presale/traces")
    public Result<?> getPresaleTraces() {
        List<Traceability> traces = traceService.list(new LambdaQueryWrapper<Traceability>()
                .eq(Traceability::getDeleted, 0)
                .orderByDesc(Traceability::getCreateTime));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Traceability t : traces) {
            long count = orderInfoService.count(new LambdaQueryWrapper<OrderInfo>()
                    .eq(OrderInfo::getTraceId, t.getId())
                    .gt(OrderInfo::getPresaleStatus, 0));
            if (count > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("trace", t);
                item.put("presaleOrderCount", count);
                OrderInfo latest = orderInfoService.getOne(new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getTraceId, t.getId())
                        .gt(OrderInfo::getPresaleStatus, 0)
                        .orderByDesc(OrderInfo::getPresaleStatus)
                        .last("LIMIT 1"));
                item.put("currentPresaleStatus", latest != null ? latest.getPresaleStatus() : 0);
                result.add(item);
            }
        }
        return Result.success(result);
    }

    private int stageToPresaleStatus(String stage) {
        if (stage == null) return 0;
        switch (stage) {
            case "seedling": case "sow": return 1;
            case "bloom": case "fruit": return 2;
            case "ripe": case "harvest": return 3;
            default: return 2;
        }
    }

    private String stageName(String stage) {
        if (stage == null) return "未知";
        switch (stage) {
            case "seedling": case "sow": return "播种";
            case "bloom": return "开花";
            case "fruit": return "结果";
            case "ripe": case "harvest": return "成熟";
            default: return stage;
        }
    }
}
