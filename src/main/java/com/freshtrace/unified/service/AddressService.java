package com.freshtrace.unified.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.freshtrace.unified.dto.AddressDTO;
import com.freshtrace.unified.entity.UserAddress;

import java.util.List;

public interface AddressService extends IService<UserAddress> {
    List<UserAddress> listByUserId(Long userId);
    UserAddress create(AddressDTO dto);
    UserAddress update(Long id, AddressDTO dto);
    void delete(Long id);
}
