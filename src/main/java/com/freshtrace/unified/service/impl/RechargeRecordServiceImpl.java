package com.freshtrace.unified.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.RechargeRecord;
import com.freshtrace.unified.mapper.RechargeRecordMapper;
import com.freshtrace.unified.service.RechargeRecordService;
import org.springframework.stereotype.Service;
@Service
public class RechargeRecordServiceImpl extends ServiceImpl<RechargeRecordMapper, RechargeRecord> implements RechargeRecordService {
}
