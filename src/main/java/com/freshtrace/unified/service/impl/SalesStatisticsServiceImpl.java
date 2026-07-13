package com.freshtrace.unified.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.SalesStatistics;
import com.freshtrace.unified.mapper.SalesStatisticsMapper;
import com.freshtrace.unified.service.SalesStatisticsService;
import org.springframework.stereotype.Service;
@Service
public class SalesStatisticsServiceImpl extends ServiceImpl<SalesStatisticsMapper, SalesStatistics> implements SalesStatisticsService {
}
