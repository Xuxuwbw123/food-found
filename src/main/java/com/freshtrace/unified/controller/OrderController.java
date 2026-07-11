package com.freshtrace.unified.controller;

import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.dto.CreateOrderDTO;
import com.freshtrace.unified.dto.OrderDetailVO;
import com.freshtrace.unified.dto.OrderVO;
import com.freshtrace.unified.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired private OrderService orderService;

    @PostMapping("/create")
    public Result<Map<String, Object>> create(@RequestBody CreateOrderDTO dto) {
        try { return Result.ok(orderService.create(dto)); }
        catch (Exception e) { return Result.fail(e.getMessage()); }
    }

    @GetMapping("/mylist")
    public Result<Page<OrderVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        return Result.ok(orderService.list(pageNum, pageSize, status));
    }

    @GetMapping("/{id}")
    public Result<OrderDetailVO> detail(@PathVariable Long id) {
        try { return Result.ok(orderService.getDetail(id)); }
        catch (Exception e) { return Result.fail(e.getMessage()); }
    }

    @PutMapping("/pay/{id}")
    public Result<Void> pay(@PathVariable Long id) {
        try { orderService.pay(id); return Result.ok(); }
        catch (Exception e) { return Result.fail(e.getMessage()); }
    }

    @PutMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        try { orderService.cancel(id); return Result.ok(); }
        catch (Exception e) { return Result.fail(e.getMessage()); }
    }

    @PutMapping("/{id}/receive")
    public Result<Void> confirmReceive(@PathVariable Long id) {
        try { orderService.confirmReceive(id); return Result.ok(); }
        catch (Exception e) { return Result.fail(e.getMessage()); }
    }
}
