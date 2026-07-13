package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.SysLocation;
import com.freshtrace.unified.mapper.SysLocationMapper;
import com.freshtrace.unified.service.SysLocationService;
import org.springframework.stereotype.Service;

@Service
public class SysLocationServiceImpl extends ServiceImpl<SysLocationMapper, SysLocation> implements SysLocationService {
}
