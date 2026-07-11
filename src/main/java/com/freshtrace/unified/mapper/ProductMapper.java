package com.freshtrace.unified.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freshtrace.unified.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
