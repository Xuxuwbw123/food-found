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
        if (f == null || f.getDeleted() == 1) return Result.error(404, "鍐滄埛涓嶅瓨鍦?);
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
            if (existing.getAuditStatus() == 0) return Result.error(400, "宸叉湁鐢宠鍦ㄥ鏍镐腑");
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
        return Result.success("鐢宠宸叉彁浜?, null);
    }

    @PutMapping("/update")
    public Result<?> updateFarmer(@RequestBody Farmer farmer, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        farmer.setUserId(userId);
        farmerService.update(farmer, new LambdaUpdateWrapper<Farmer>().eq(Farmer::getUserId, userId));
        return Result.success("鏇存柊鎴愬姛", null);
    }

    // ============ 鍐滄埛璁㈠崟 ============
    @GetMapping("/order/list")
    public Result<?> farmerOrderList(HttpServletRequest request, PageQuery pageQuery,
            @RequestParam(required = false) Integer status) {
        Long userId = (Long) request.getAttribute("userId");
        Farmer f = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        if (f == null) return Result.error(403, "闈炲啘鎴?);

        List<Long> orderIds = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getFarmerId, f.getId()))
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
        if (f == null) return Result.error(403, "闈炲啘鎴?);
        OrderInfo order = orderService.getById(id);
        if (order == null) return Result.error(404, "璁㈠崟涓嶅瓨鍦?);
        order.setItems(orderItemService.list(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, id)));
        order.setLogs(orderLogService.list(new LambdaQueryWrapper<OrderLog>().eq(OrderLog::getOrderId, id)));
        return Result.success(order);
    }

    @PutMapping("/order/deliver/{id}")
    public Result<?> deliverOrder(@PathVariable Long id, @RequestBody Map<String, String> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Farmer f = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        if (f == null) return Result.error(403, "闈炲啘鎴?);
        OrderInfo order = orderService.getById(id);
        if (order == null || order.getOrderStatus() != 1) return Result.error(400, "璁㈠崟鐘舵€佷笉鍏佽鍙戣揣");
        order.setOrderStatus(2); order.setLogisticsNo(body.get("logisticsNo"));
        order.setLogisticsCompany(body.get("logisticsCompany")); order.setDeliveryTime(LocalDateTime.now());
        orderService.updateById(order);
        OrderLog log = new OrderLog();
        log.setOrderId(id); log.setOrderNo(order.getOrderNo()); log.setOrderStatus(2);
        log.setOperatorType(2); log.setRemark("鍐滄埛鍙戣揣:" + body.getOrDefault("logisticsNo", ""));
        orderLogService.save(log);
        return Result.success("鍙戣揣鎴愬姛", null);
    }

    // ============ 鍐滄埛鍟嗗搧 ============
    @GetMapping("/product/list")
    public Result<?> farmerProductList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Farmer f = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        if (f == null) return Result.error(403, "闈炲啘鎴?);
        return Result.success(productService.list(new LambdaQueryWrapper<Product>()
                .eq(Product::getFarmerId, f.getId()).eq(Product::getDeleted, 0).orderByDesc(Product::getCreateTime)));
    }

    @PutMapping("/product/updateStock/{id}")
    public Result<?> updateStock(@PathVariable Long id, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Farmer f = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        Product p = productService.getById(id);
        if (f == null || p == null || !p.getFarmerId().equals(f.getId())) return Result.error(403, "鏃犳潈鎿嶄綔");
        int stock = Integer.parseInt(body.get("stock").toString());
        if (stock < 0) return Result.error(400, "搴撳瓨涓嶈兘涓鸿礋");
        p.setStock(stock); productService.updateById(p);
        return Result.success("搴撳瓨宸叉洿鏂?, null);
    }

    @PutMapping("/product/changeStatus/{id}")
    public Result<?> changeProductStatus(@PathVariable Long id, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Farmer f = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        Product p = productService.getById(id);
        if (f == null || p == null || !p.getFarmerId().equals(f.getId())) return Result.error(403, "鏃犳潈鎿嶄綔");
        if (p.getAuditStatus() != 1) return Result.error(400, "瀹℃牳閫氳繃鍚庢墠鑳戒笂涓嬫灦");
        p.setStatus(Integer.parseInt(body.get("status").toString()));
        productService.updateById(p);
        return Result.success("鐘舵€佸凡鏇存柊", null);
    }

    @PutMapping("/product/batch")
    public Result<?> batchProduct(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Farmer f = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        if (f == null) return Result.error(403, "闈炲啘鎴?);
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
        return Result.success("鎵归噺鎿嶄綔瀹屾垚", null);
    }

    // ============ 鍐滄埛缁熻 ============
    @GetMapping("/statistics/overview")
    public Result<?> farmerStatistics(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Farmer f = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        if (f == null) return Result.error(403, "闈炲啘鎴?);

        long productCount = productService.count(new LambdaQueryWrapper<Product>()
                .eq(Product::getFarmerId, f.getId()).eq(Product::getDeleted, 0));
        Map<String, Object> data = new HashMap<>();
        data.put("productCount", productCount);
        data.put("todayOrders", 0); data.put("todaySales", 0);
        data.put("totalOrders", 0); data.put("totalSales", 0);
        data.put("trend7", Collections.emptyList()); data.put("top10", Collections.emptyList());
        return Result.success(data);
    }

    // ============ 鍐滄埛鍞悗 ============
    @GetMapping("/aftersales/list")
    public Result<?> farmerAfterSalesList(HttpServletRequest request, PageQuery pageQuery,
            @RequestParam(required = false) Integer status) {
        Long userId = (Long) request.getAttribute("userId");
        Farmer f = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        if (f == null) return Result.error(403, "闈炲啘鎴?);
        LambdaQueryWrapper<AfterSalesOrder> qw = new LambdaQueryWrapper<AfterSalesOrder>()
                .eq(AfterSalesOrder::getFarmerId, f.getId());
        if (status != null) qw.eq(AfterSalesOrder::getStatus, status);
        qw.orderByDesc(AfterSalesOrder::getApplyTime);
        return Result.success(afterSalesService.page(pageQuery.toPage(), qw));
    }

    @PutMapping("/aftersales/audit/{id}")
    public Result<?> auditAfterSales(@PathVariable Long id, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Farmer f = farmerService.getOne(new LambdaQueryWrapper<Farmer>().eq(Farmer::getUserId, userId));
        if (f == null) return Result.error(403, "闈炲啘鎴?);
        AfterSalesOrder aso = afterSalesService.getById(id);
        if (aso == null) return Result.error(404, "鍞悗鍗曚笉瀛樺湪");
        boolean agree = Boolean.TRUE.equals(body.get("agree"));
        aso.setStatus(agree ? 1 : 3);
        aso.setAdminRemark((String) body.getOrDefault("remark", ""));
        aso.setAuditTime(LocalDateTime.now());
        afterSalesService.updateById(aso);
        return Result.success("澶勭悊瀹屾垚", null);
    }
}
