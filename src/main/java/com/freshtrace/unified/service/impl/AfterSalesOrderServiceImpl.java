package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.AfterSalesOrder;
import com.freshtrace.unified.mapper.AfterSalesOrderMapper;
import com.freshtrace.unified.service.AfterSalesOrderService;
import org.springframework.stereotype.Service;

@Service
public class AfterSalesOrderServiceImpl extends ServiceImpl<AfterSalesOrderMapper, AfterSalesOrder> implements AfterSalesOrderService {
}
