package com.freshtrace.unified.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.common.UserContext;
import com.freshtrace.unified.entity.UserAddress;
import com.freshtrace.unified.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired private AddressService addressService;

    @GetMapping("/list")
    public Result<List<UserAddress>> list() {
        Long userId = UserContext.getUserId();
        List<UserAddress> list = addressService.list(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId).eq(UserAddress::getDeleted, 0)
                .orderByDesc(UserAddress::getIsDefault));
        return Result.ok(list);
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody UserAddress addr) {
        addr.setUserId(UserContext.getUserId());
        if (addr.getIsDefault() != null && addr.getIsDefault() == 1) resetDefault(addr.getUserId());
        addressService.save(addr);
        return Result.ok();
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody UserAddress addr) {
        addr.setUserId(UserContext.getUserId());
        if (addr.getIsDefault() != null && addr.getIsDefault() == 1) resetDefault(addr.getUserId());
        addressService.updateById(addr);
        return Result.ok();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        addressService.remove(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getId, id).eq(UserAddress::getUserId, UserContext.getUserId()));
        return Result.ok();
    }

    @PutMapping("/default/{id}")
    public Result<Void> setDefault(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        resetDefault(userId);
        UserAddress addr = addressService.getById(id);
        if (addr != null) { addr.setIsDefault(1); addressService.updateById(addr); }
        return Result.ok();
    }

    private void resetDefault(Long userId) {
        List<UserAddress> defaults = addressService.list(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId).eq(UserAddress::getIsDefault, 1));
        for (UserAddress a : defaults) { a.setIsDefault(0); addressService.updateById(a); }
    }
}
