package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.MarketingActivity;
import com.freshtrace.unified.mapper.MarketingActivityMapper;
import com.freshtrace.unified.service.MarketingActivityService;
import org.springframework.stereotype.Service;

@Service
public class MarketingActivityServiceImpl extends ServiceImpl<MarketingActivityMapper, MarketingActivity> implements MarketingActivityService {
}
