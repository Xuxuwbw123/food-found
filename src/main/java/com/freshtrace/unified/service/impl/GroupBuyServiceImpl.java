package com.freshtrace.unified.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.entity.GroupBuy;
import com.freshtrace.unified.mapper.GroupBuyMapper;
import com.freshtrace.unified.service.GroupBuyService;
import org.springframework.stereotype.Service;
@Service
public class GroupBuyServiceImpl extends ServiceImpl<GroupBuyMapper, GroupBuy> implements GroupBuyService {
}
