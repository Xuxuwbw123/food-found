package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.TraceLogistics;
import com.freshtrace.unified.mapper.TraceLogisticsMapper;
import com.freshtrace.unified.service.TraceLogisticsService;
import org.springframework.stereotype.Service;

@Service
public class TraceLogisticsServiceImpl extends ServiceImpl<TraceLogisticsMapper, TraceLogistics> implements TraceLogisticsService {
}
