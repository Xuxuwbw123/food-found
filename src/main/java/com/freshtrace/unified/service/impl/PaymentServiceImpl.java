package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshtrace.unified.entity.PaymentInfo;
import com.freshtrace.unified.mapper.PaymentInfoMapper;
import com.freshtrace.unified.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Override
    public Map<String, Object> getStatus(String orderNo) {
        PaymentInfo pay = paymentInfoMapper.selectOne(
                new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderNo, orderNo));
        Map<String, Object> result = new HashMap<>();
        if (pay == null) {
            result.put("payStatus", -1);
            return result;
        }
        result.put("payStatus", pay.getPayStatus());
        result.put("payAmount", pay.getPayAmount());
        result.put("payTime", pay.getPayTime());
        result.put("paymentNo", pay.getPaymentNo());
        return result;
    }
}
