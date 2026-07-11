package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.TraceProcessing;
import com.freshtrace.unified.mapper.TraceProcessingMapper;
import com.freshtrace.unified.service.TraceProcessingService;
import org.springframework.stereotype.Service;

@Service
public class TraceProcessingServiceImpl extends ServiceImpl<TraceProcessingMapper, TraceProcessing> implements TraceProcessingService {
}
