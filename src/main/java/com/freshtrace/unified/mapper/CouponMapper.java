package com.freshtrace.unified.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freshtrace.unified.entity.Coupon;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {

    /**
     * Bug #16 fix: 原子领券
     */
    @Update("UPDATE coupon SET taken_count = COALESCE(taken_count, 0) + 1 " +
            "WHERE id = #{couponId} AND status = 1 AND taken_count < total_count " +
            "AND (start_time IS NULL OR start_time <= NOW()) " +
            "AND (end_time IS NULL OR end_time > NOW())")
    int atomicTake(@Param("couponId") Long couponId);
}
