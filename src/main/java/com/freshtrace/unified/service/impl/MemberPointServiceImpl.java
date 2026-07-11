package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.MemberPoint;
import com.freshtrace.unified.mapper.MemberPointMapper;
import com.freshtrace.unified.service.MemberPointService;
import org.springframework.stereotype.Service;

@Service
public class MemberPointServiceImpl extends ServiceImpl<MemberPointMapper, MemberPoint> implements MemberPointService {
}
