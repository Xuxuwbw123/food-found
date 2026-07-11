package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.TraceStorage;
import com.freshtrace.unified.mapper.TraceStorageMapper;
import com.freshtrace.unified.service.TraceStorageService;
import org.springframework.stereotype.Service;

@Service
public class TraceStorageServiceImpl extends ServiceImpl<TraceStorageMapper, TraceStorage> implements TraceStorageService {
}
