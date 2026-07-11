package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.UserFootprint;
import com.freshtrace.unified.mapper.UserFootprintMapper;
import com.freshtrace.unified.service.UserFootprintService;
import org.springframework.stereotype.Service;

@Service
public class UserFootprintServiceImpl extends ServiceImpl<UserFootprintMapper, UserFootprint> implements UserFootprintService {
}
