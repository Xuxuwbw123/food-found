package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.TraceScanLog;
import com.freshtrace.unified.mapper.TraceScanLogMapper;
import com.freshtrace.unified.service.TraceScanLogService;
import org.springframework.stereotype.Service;

@Service
public class TraceScanLogServiceImpl extends ServiceImpl<TraceScanLogMapper, TraceScanLog> implements TraceScanLogService {
}
