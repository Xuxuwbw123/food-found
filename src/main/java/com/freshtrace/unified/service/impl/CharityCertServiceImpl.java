package com.freshtrace.unified.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.CharityCert;
import com.freshtrace.unified.mapper.CharityCertMapper;
import com.freshtrace.unified.service.CharityCertService;
import org.springframework.stereotype.Service;
@Service
public class CharityCertServiceImpl extends ServiceImpl<CharityCertMapper, CharityCert> implements CharityCertService {
}
