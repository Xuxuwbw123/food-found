package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.TraceImage;
import com.freshtrace.unified.mapper.TraceImageMapper;
import com.freshtrace.unified.service.TraceImageService;
import org.springframework.stereotype.Service;

@Service
public class TraceImageServiceImpl extends ServiceImpl<TraceImageMapper, TraceImage> implements TraceImageService {
}
