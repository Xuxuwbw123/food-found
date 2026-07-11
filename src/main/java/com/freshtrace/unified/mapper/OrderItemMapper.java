package com.freshtrace.unified.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freshtrace.unified.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
