package com.freshtrace.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.entity.CustomerServiceLog;
import com.freshtrace.unified.service.CustomerServiceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/admin/cs-log", "/api/admin/cs-log"})
public class CustomerServiceLogController {

    @Autowired private CustomerServiceLogService csLogService;

    @GetMapping("/list/{afterSalesId}")
    public Result<List<CustomerServiceLog>> list(@PathVariable Long afterSalesId) {
        List<CustomerServiceLog> logs = csLogService.list(new LambdaQueryWrapper<CustomerServiceLog>()
                .eq(CustomerServiceLog::getAfterSalesId, afterSalesId)
                .orderByAsc(CustomerServiceLog::getCreateTime));
        return Result.ok(logs);
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Map<String, Object> body) {
        CustomerServiceLog log = new CustomerServiceLog();
        log.setAfterSalesId(Long.valueOf(body.get("afterSalesId").toString()));
        if (body.containsKey("orderId")) log.setOrderId(Long.valueOf(body.get("orderId").toString()));
        if (body.containsKey("userId")) log.setUserId(Long.valueOf(body.get("userId").toString()));
        log.setOperatorType(Integer.parseInt(body.getOrDefault("operatorType", "1").toString()));
        log.setMsgType(Integer.parseInt(body.getOrDefault("msgType", "1").toString()));
        log.setContent((String) body.getOrDefault("content", ""));
        log.setCreateTime(LocalDateTime.now());
        csLogService.save(log);
        return Result.ok();
    }
}
