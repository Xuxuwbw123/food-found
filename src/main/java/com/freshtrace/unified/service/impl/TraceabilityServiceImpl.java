package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.Traceability;
import com.freshtrace.unified.mapper.TraceabilityMapper;
import com.freshtrace.unified.service.TraceabilityService;
import org.springframework.stereotype.Service;

@Service
public class TraceabilityServiceImpl extends ServiceImpl<TraceabilityMapper, Traceability> implements TraceabilityService {
}
