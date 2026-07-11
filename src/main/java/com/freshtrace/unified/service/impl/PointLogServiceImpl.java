package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.PointLog;
import com.freshtrace.unified.mapper.PointLogMapper;
import com.freshtrace.unified.service.PointLogService;
import org.springframework.stereotype.Service;

@Service
public class PointLogServiceImpl extends ServiceImpl<PointLogMapper, PointLog> implements PointLogService {
}
