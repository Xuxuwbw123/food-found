package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.Farmer;
import com.freshtrace.unified.mapper.FarmerMapper;
import com.freshtrace.unified.service.FarmerService;
import org.springframework.stereotype.Service;

@Service
public class FarmerServiceImpl extends ServiceImpl<FarmerMapper, Farmer> implements FarmerService {
}
