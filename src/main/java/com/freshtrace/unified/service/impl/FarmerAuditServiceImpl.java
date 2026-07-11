package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.FarmerAudit;
import com.freshtrace.unified.mapper.FarmerAuditMapper;
import com.freshtrace.unified.service.FarmerAuditService;
import org.springframework.stereotype.Service;

@Service
public class FarmerAuditServiceImpl extends ServiceImpl<FarmerAuditMapper, FarmerAudit> implements FarmerAuditService {
}
