package com.freshtrace.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.entity.*;
import com.freshtrace.unified.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class AdminCertFarmController {

    @Autowired private QualificationCertService certService;
    @Autowired private FarmUpdateService farmUpdateService;

    // ============ 资质证书管理 ============
    @GetMapping("/admin/qualification-certs")
    public Result<?> certList() {
        return Result.success(certService.list());
    }

    @PostMapping("/admin/qualification-certs")
    public Result<?> certAdd(@RequestBody QualificationCert cert) {
        cert.setCreateTime(LocalDateTime.now());
        certService.save(cert);
        return Result.success();
    }

    @PutMapping("/admin/qualification-certs")
    public Result<?> certUpdate(@RequestBody QualificationCert cert) {
        certService.updateById(cert);
        return Result.success();
    }

    @DeleteMapping("/admin/qualification-certs/{id}")
    public Result<?> certDelete(@PathVariable Long id) {
        certService.removeById(id);
        return Result.success();
    }

    // ============ 农场动态管理 ============
    @GetMapping("/admin/farm-updates")
    public Result<?> farmUpdateList() {
        return Result.success(farmUpdateService.list());
    }

    @PostMapping("/admin/farm-updates")
    public Result<?> farmUpdateAdd(@RequestBody FarmUpdate update) {
        update.setCreateTime(LocalDateTime.now());
        farmUpdateService.save(update);
        return Result.success();
    }

    @PutMapping("/admin/farm-updates")
    public Result<?> farmUpdateUpdate(@RequestBody FarmUpdate update) {
        farmUpdateService.updateById(update);
        return Result.success();
    }

    @DeleteMapping("/admin/farm-updates/{id}")
    public Result<?> farmUpdateDelete(@PathVariable Long id) {
        farmUpdateService.removeById(id);
        return Result.success();
    }
}
