package com.freshtrace.unified.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.freshtrace.unified.entity.Coupon;
import com.freshtrace.unified.mapper.CouponMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Component
public class CouponExpireTask {
    @Autowired private CouponMapper couponMapper;

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void expireCoupons() {
        int affected = couponMapper.update(null,
                new LambdaUpdateWrapper<Coupon>()
                        .set(Coupon::getStatus, 0)
                        .eq(Coupon::getStatus, 1)
                        .lt(Coupon::getEndTime, LocalDateTime.now()));
        if (affected > 0) System.out.println("[CouponExpireTask] 已过期 " + affected + " 张优惠券");
    }
}