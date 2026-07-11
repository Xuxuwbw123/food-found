package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.TraceHarvest;
import com.freshtrace.unified.mapper.TraceHarvestMapper;
import com.freshtrace.unified.service.TraceHarvestService;
import org.springframework.stereotype.Service;

@Service
public class TraceHarvestServiceImpl extends ServiceImpl<TraceHarvestMapper, TraceHarvest> implements TraceHarvestService {
}
