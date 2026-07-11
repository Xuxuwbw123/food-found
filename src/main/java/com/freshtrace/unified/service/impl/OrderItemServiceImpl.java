package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.OrderItem;
import com.freshtrace.unified.mapper.OrderItemMapper;
import com.freshtrace.unified.service.OrderItemService;
import org.springframework.stereotype.Service;

@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {
}
