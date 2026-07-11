package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.LogisticsTrack;
import com.freshtrace.unified.mapper.LogisticsTrackMapper;
import com.freshtrace.unified.service.LogisticsTrackService;
import org.springframework.stereotype.Service;

@Service
public class LogisticsTrackServiceImpl extends ServiceImpl<LogisticsTrackMapper, LogisticsTrack> implements LogisticsTrackService {
}
