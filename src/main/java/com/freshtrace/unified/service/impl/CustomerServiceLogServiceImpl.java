package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.CustomerServiceLog;
import com.freshtrace.unified.mapper.CustomerServiceLogMapper;
import com.freshtrace.unified.service.CustomerServiceLogService;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceLogServiceImpl extends ServiceImpl<CustomerServiceLogMapper, CustomerServiceLog> implements CustomerServiceLogService {
}
