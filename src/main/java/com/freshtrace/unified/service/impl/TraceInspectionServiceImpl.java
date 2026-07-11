package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.TraceInspection;
import com.freshtrace.unified.mapper.TraceInspectionMapper;
import com.freshtrace.unified.service.TraceInspectionService;
import org.springframework.stereotype.Service;

@Service
public class TraceInspectionServiceImpl extends ServiceImpl<TraceInspectionMapper, TraceInspection> implements TraceInspectionService {
}
