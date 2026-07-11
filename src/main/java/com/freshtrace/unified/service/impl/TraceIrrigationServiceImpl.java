package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.TraceIrrigation;
import com.freshtrace.unified.mapper.TraceIrrigationMapper;
import com.freshtrace.unified.service.TraceIrrigationService;
import org.springframework.stereotype.Service;

@Service
public class TraceIrrigationServiceImpl extends ServiceImpl<TraceIrrigationMapper, TraceIrrigation> implements TraceIrrigationService {
}
