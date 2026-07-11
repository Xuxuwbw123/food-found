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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/farmer")
public class FarmerController {

    @Autowired private FarmerService farmerService;
    @Autowired private FarmerAuditService farmerAuditService;
    @Autowired private OrderInfoService orderService;
    @Autowired private OrderItemService orderItemService;
    @Autowired private OrderLogService orderLogService;
    @Autowired private ProductService productService;
    @Autowired private AfterSalesOrderService afterSalesService;
    @Autowired private SysNoticeService noticeService;

    @GetMapping("/list")
    public Result<?> farmerList() {
        return Result.success(farmerService.list(new LambdaQueryWrapper<Farmer>()
                .eq(Farmer::getDeleted, 0).orderByDesc(Farmer::getCreateTime)));
    }

    @GetMapping("/detail/{id}")
    public Result<?> farmerDetail(@PathVariable Long id) {
        Farmer f = farmerService.getById(id);
        if (f == null || f.getDeleted() == 1) return Result.error(404, "农户不存在");
        return Result.success(f);
    }

    @GetMapping("/info/{userId}")
    public Result<?> farmerInfo(@PathVariable Long userId) {
        Farmer f = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        return Result.success(f);
    }

    @GetMapping("/auth-info/{userId}")
    public Result<?> farmerAuthInfo(@PathVariable Long userId) {
        Farmer f = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        return Result.success(f);
    }

    @PostMapping("/apply")
    public Result<?> applyFarmer(@RequestBody Farmer farmer, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Farmer existing = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        if (existing != null) {
            if (existing.getAuditStatus() == 0) return Result.error(400, "已有申请在审核中");
            existing.setFarmerName(farmer.getFarmerName());
            existing.setContactPerson(farmer.getContactPerson());
            existing.setContactPhone(farmer.getContactPhone());
            existing.setProvince(farmer.getProvince());
            existing.setCity(farmer.getCity());
            existing.setDistrict(farmer.getDistrict());
            existing.setAddress(farmer.getAddress());
            existing.setFarmArea(farmer.getFarmArea());
            existing.setMainProducts(farmer.getMainProducts());
            existing.setAuditStatus(0); existing.setAuditRemark(null); existing.setAuditTime(null);
            farmerService.updateById(existing);
            FarmerAudit fa = new FarmerAudit();
            fa.setFarmerId(existing.getId()); fa.setAuditStatus(0); fa.setCreateTime(LocalDateTime.now());
            farmerAuditService.save(fa);
        } else {
            farmer.setUserId(userId); farmer.setAuditStatus(0);
            farmerService.save(farmer);
            FarmerAudit fa = new FarmerAudit();
            fa.setFarmerId(farmer.getId()); fa.setAuditStatus(0); fa.setCreateTime(LocalDateTime.now());
            farmerAuditService.save(fa);
        }
        return Result.success("申请已提交", null);
    }

    @PutMapping("/update")
    public Result<?> updateFarmer(@RequestBody Farmer farmer, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        farmer.setUserId(userId);
        farmerService.update(farmer, new LambdaUpdateWrapper<Farmer>().eq(Farmer::getUserId, userId));
        return Result.success("更新成功", null);
    }

    // ============ 农户订单 ============
    @GetMapping("/order/list")
    public Result<?> farmerOrderList(HttpServletRequest request, PageQuery pageQuery,
            @RequestParam(required = false) Integer status) {
        Long userId = (Long) request.getAttribute("userId");
        Farmer f = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        if (f == null) return Result.error(403, "非农户");

        List<Long> orderIds = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                .inSql(OrderItem::getProductId, "SELECT id FROM product WHERE farmer_id=" + f.getId()))
                .stream().map(OrderItem::getOrderId).distinct().collect(java.util.stream.Collectors.toList());
        if (orderIds.isEmpty()) return Result.success(new Page<>());

        LambdaQueryWrapper<OrderInfo> qw = new LambdaQueryWrapper<OrderInfo>()
                .in(OrderInfo::getId, orderIds).eq(OrderInfo::getDeleted, 0);
        if (status != null) qw.eq(OrderInfo::getOrderStatus, status);
        qw.orderByDesc(OrderInfo::getCreateTime);
        Page<OrderInfo> page = orderService.page(pageQuery.toPage(), qw);
        for (OrderInfo o : page.getRecords()) {
            o.setItems(orderItemService.list(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, o.getId())));
        }
        return Result.success(page);
    }

    @GetMapping("/order/detail/{id}")
    public Result<?> farmerOrderDetail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Farmer f = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        if (f == null) return Result.error(403, "非农户");
        OrderInfo order = orderService.getById(id);
        if (order == null) return Result.error(404, "订单不存在");
        order.setItems(orderItemService.list(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, id)));
        order.setLogs(orderLogService.list(new LambdaQueryWrapper<OrderLog>().eq(OrderLog::getOrderId, id)));
        return Result.success(order);
    }

    @PutMapping("/order/deliver/{id}")
    public Result<?> deliverOrder(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Farmer f = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        if (f == null) return Result.error(403, "非农户");
        OrderInfo order = orderService.getById(id);
        if (order == null || order.getOrderStatus() != 1) return Result.error(400, "订单状态不允许发货");
        order.setOrderStatus(2); order.setLogisticsNo(body.get("logisticsNo"));
        order.setLogisticsCompany(body.get("logisticsCompany")); order.setDeliveryTime(LocalDateTime.now());
        orderService.updateById(order);
        OrderLog log = new OrderLog();
        log.setOrderId(id); log.setOrderNo(order.getOrderNo()); log.setOrderStatus(2);
        log.setOperatorType(2); log.setRemark("农户发货:" + body.getOrDefault("logisticsNo", ""));
        orderLogService.save(log);
        return Result.success("发货成功", null);
    }

    // ============ 农户商品 ============
    @GetMapping("/product/list")
    public Result<?> farmerProductList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Farmer f = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        if (f == null) return Result.error(403, "非农户");
        return Result.success(productService.list(new LambdaQueryWrapper<Product>()
                .eq(Product::getFarmerId, f.getId()).eq(Product::getDeleted, 0).orderByDesc(Product::getCreateTime)));
    }

    @PutMapping("/product/updateStock/{id}")
    public Result<?> updateStock(@PathVariable Long id, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Farmer f = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        Product p = productService.getById(id);
        if (f == null || p == null || !p.getFarmerId().equals(f.getId())) return Result.error(403, "无权操作");
        int stock = Integer.parseInt(body.get("stock").toString());
        if (stock < 0) return Result.error(400, "库存不能为负");
        p.setStock(stock); productService.updateById(p);
        return Result.success("库存已更新", null);
    }

    @PutMapping("/product/changeStatus/{id}")
    public Result<?> changeProductStatus(@PathVariable Long id, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Farmer f = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        Product p = productService.getById(id);
        if (f == null || p == null || !p.getFarmerId().equals(f.getId())) return Result.error(403, "无权操作");
        if (p.getAuditStatus() != 1) return Result.error(400, "审核通过后才能上下架");
        p.setStatus(Integer.parseInt(body.get("status").toString()));
        productService.updateById(p);
        return Result.success("状态已更新", null);
    }

    @PutMapping("/product/batch")
    public Result<?> batchProduct(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Farmer f = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        if (f == null) return Result.error(403, "非农户");
        @SuppressWarnings("unchecked")
        List<Long> ids = ((List<Number>) body.get("ids")).stream().map(Number::longValue).collect(java.util.stream.Collectors.toList());
        String action = (String) body.get("action");
        if ("online".equals(action)) {
            productService.update(new LambdaUpdateWrapper<Product>().in(Product::getId, ids).eq(Product::getFarmerId, f.getId()).set(Product::getStatus, 1));
        } else if ("offline".equals(action)) {
            productService.update(new LambdaUpdateWrapper<Product>().in(Product::getId, ids).eq(Product::getFarmerId, f.getId()).set(Product::getStatus, 0));
        } else if ("updateStock".equals(action)) {
            int stock = Integer.parseInt(body.get("stock").toString());
            productService.update(new LambdaUpdateWrapper<Product>().in(Product::getId, ids).eq(Product::getFarmerId, f.getId()).set(Product::getStock, stock));
        }
        return Result.success("批量操作完成", null);
    }

    // ============ 农户统计 ============
    @GetMapping("/statistics/overview")
    public Result<?> farmerStatistics(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Farmer f = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        if (f == null) return Result.error(403, "非农户");

        long productCount = productService.count(new LambdaQueryWrapper<Product>()
                .eq(Product::getFarmerId, f.getId()).eq(Product::getDeleted, 0));
        Map<String, Object> data = new HashMap<>();
        data.put("productCount", productCount);
        data.put("todayOrders", 0); data.put("todaySales", 0);
        data.put("totalOrders", 0); data.put("totalSales", 0);
        data.put("trend7", Collections.emptyList()); data.put("top10", Collections.emptyList());
        return Result.success(data);
    }

    // ============ 农户售后 ============
    @GetMapping("/aftersales/list")
    public Result<?> farmerAfterSalesList(HttpServletRequest request, PageQuery pageQuery,
            @RequestParam(required = false) Integer status) {
        Long userId = (Long) request.getAttribute("userId");
        Farmer f = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        if (f == null) return Result.error(403, "非农户");
        LambdaQueryWrapper<AfterSalesOrder> qw = new LambdaQueryWrapper<AfterSalesOrder>()
                .inSql(AfterSalesOrder::getProductId, "SELECT id FROM product WHERE farmer_id=" + f.getId());
        if (status != null) qw.eq(AfterSalesOrder::getStatus, status);
        qw.orderByDesc(AfterSalesOrder::getApplyTime);
        return Result.success(afterSalesService.page(pageQuery.toPage(), qw));
    }

    @PutMapping("/aftersales/audit/{id}")
    public Result<?> auditAfterSales(@PathVariable Long id, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Farmer f = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        if (f == null) return Result.error(403, "非农户");
        AfterSalesOrder aso = afterSalesService.getById(id);
        if (aso == null) return Result.error(404, "售后单不存在");
        boolean agree = Boolean.TRUE.equals(body.get("agree"));
        aso.setStatus(agree ? 1 : 3);
        aso.setAdminRemark((String) body.getOrDefault("remark", ""));
        aso.setAuditTime(LocalDateTime.now());
        afterSalesService.updateById(aso);
        return Result.success("处理完成", null);
    }
}
