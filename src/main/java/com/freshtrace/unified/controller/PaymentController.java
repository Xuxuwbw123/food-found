package com.freshtrace.unified.controller;

import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired private PaymentService paymentService;

    @GetMapping("/status")
    public Result<Map<String, Object>> getStatus(@RequestParam String orderNo) {
        try { return Result.ok(paymentService.getStatus(orderNo)); }
        catch (Exception e) { return Result.fail(e.getMessage()); }
    }
}
