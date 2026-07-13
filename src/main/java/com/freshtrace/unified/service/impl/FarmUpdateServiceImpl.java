package com.freshtrace.unified.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.FarmUpdate;
import com.freshtrace.unified.mapper.FarmUpdateMapper;
import com.freshtrace.unified.service.FarmUpdateService;
import org.springframework.stereotype.Service;
@Service
public class FarmUpdateServiceImpl extends ServiceImpl<FarmUpdateMapper, FarmUpdate> implements FarmUpdateService {
}
