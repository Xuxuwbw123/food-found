package com.freshtrace.unified.service;

import java.util.Map;

public interface PaymentService {
    Map<String, Object> getStatus(String orderNo);
}
