package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.Seckill;
import com.freshtrace.unified.mapper.SeckillMapper;
import com.freshtrace.unified.service.SeckillService;
import org.springframework.stereotype.Service;

@Service
public class SeckillServiceImpl extends ServiceImpl<SeckillMapper, Seckill> implements SeckillService {
}
