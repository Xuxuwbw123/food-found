package com.freshtrace.unified.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.StockAlert;
import com.freshtrace.unified.mapper.StockAlertMapper;
import com.freshtrace.unified.service.StockAlertService;
import org.springframework.stereotype.Service;
@Service
public class StockAlertServiceImpl extends ServiceImpl<StockAlertMapper, StockAlert> implements StockAlertService {
}
