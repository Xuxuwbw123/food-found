package com.freshtrace.unified.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.ThemeZone;
import com.freshtrace.unified.mapper.ThemeZoneMapper;
import com.freshtrace.unified.service.ThemeZoneService;
import org.springframework.stereotype.Service;
@Service
public class ThemeZoneServiceImpl extends ServiceImpl<ThemeZoneMapper, ThemeZone> implements ThemeZoneService {
}
