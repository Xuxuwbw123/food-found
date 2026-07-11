package com.freshtrace.unified.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshtrace.unified.dto.CreateOrderDTO;
import com.freshtrace.unified.dto.OrderDetailVO;
import com.freshtrace.unified.dto.OrderVO;
import java.util.Map;

public interface OrderService {
    Map<String, Object> create(CreateOrderDTO dto);
    Page<OrderVO> list(Integer pageNum, Integer pageSize, Integer status);
    OrderDetailVO getDetail(Long id);
    void cancel(Long id);
    void confirmReceive(Long id);
    void pay(Long id);
}
