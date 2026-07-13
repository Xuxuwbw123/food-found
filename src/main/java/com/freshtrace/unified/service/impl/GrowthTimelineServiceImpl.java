package com.freshtrace.unified.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.GrowthTimeline;
import com.freshtrace.unified.mapper.GrowthTimelineMapper;
import com.freshtrace.unified.service.GrowthTimelineService;
import org.springframework.stereotype.Service;
@Service
public class GrowthTimelineServiceImpl extends ServiceImpl<GrowthTimelineMapper, GrowthTimeline> implements GrowthTimelineService {
}
