package com.freshtrace.unified.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freshtrace.unified.entity.Product;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * Bug #33/#35 fix: 原子扣库存 + 加销量
     * 用 WHERE stock >= qty 保证并发下不超卖
     */
    @Update("UPDATE product SET stock = stock - #{qty}, sales = COALESCE(sales, 0) + #{qty} " +
            "WHERE id = #{productId} AND deleted = 0 AND stock >= #{qty}")
    int deductStock(@Param("productId") Long productId, @Param("qty") Integer qty);
}
