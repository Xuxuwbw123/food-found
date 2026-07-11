package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.TracePesticide;
import com.freshtrace.unified.mapper.TracePesticideMapper;
import com.freshtrace.unified.service.TracePesticideService;
import org.springframework.stereotype.Service;

@Service
public class TracePesticideServiceImpl extends ServiceImpl<TracePesticideMapper, TracePesticide> implements TracePesticideService {
}
