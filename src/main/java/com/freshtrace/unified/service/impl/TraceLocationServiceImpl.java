package com.freshtrace.unified.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.TraceLocation;
import com.freshtrace.unified.mapper.TraceLocationMapper;
import com.freshtrace.unified.service.TraceLocationService;
import org.springframework.stereotype.Service;
@Service
public class TraceLocationServiceImpl extends ServiceImpl<TraceLocationMapper, TraceLocation> implements TraceLocationService {
}
