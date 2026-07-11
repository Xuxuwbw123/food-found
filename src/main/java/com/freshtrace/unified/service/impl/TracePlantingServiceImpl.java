package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.TracePlanting;
import com.freshtrace.unified.mapper.TracePlantingMapper;
import com.freshtrace.unified.service.TracePlantingService;
import org.springframework.stereotype.Service;

@Service
public class TracePlantingServiceImpl extends ServiceImpl<TracePlantingMapper, TracePlanting> implements TracePlantingService {
}
