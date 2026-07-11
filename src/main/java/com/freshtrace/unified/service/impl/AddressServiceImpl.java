package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.common.UserContext;
import com.freshtrace.unified.dto.AddressDTO;
import com.freshtrace.unified.entity.UserAddress;
import com.freshtrace.unified.mapper.UserAddressMapper;
import com.freshtrace.unified.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements AddressService {

    @Override
    public List<UserAddress> listByUserId(Long userId) {
        return baseMapper.selectList(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .orderByDesc(UserAddress::getIsDefault).orderByDesc(UserAddress::getCreateTime));
    }

    @Override
    @Transactional
    public UserAddress create(AddressDTO dto) {
        UserAddress addr = new UserAddress();
        copyFields(dto, addr);
        addr.setUserId(UserContext.getUserId());
        addr.setCreateTime(LocalDateTime.now());
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) clearDefault();
        baseMapper.insert(addr);
        return addr;
    }

    @Override
    @Transactional
    public UserAddress update(Long id, AddressDTO dto) {
        UserAddress addr = baseMapper.selectById(id);
        if (addr == null || !addr.getUserId().equals(UserContext.getUserId())) throw new RuntimeException("address not found");
        copyFields(dto, addr);
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) clearDefault();
        baseMapper.updateById(addr);
        return addr;
    }

    @Override
    public void delete(Long id) {
        UserAddress addr = baseMapper.selectById(id);
        if (addr == null || !addr.getUserId().equals(UserContext.getUserId())) throw new RuntimeException("address not found");
        baseMapper.deleteById(id);
    }

    private void clearDefault() {
        var list = baseMapper.selectList(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, UserContext.getUserId()).eq(UserAddress::getIsDefault, 1));
        for (var a : list) { a.setIsDefault(0); baseMapper.updateById(a); }
    }

    private void copyFields(AddressDTO dto, UserAddress addr) {
        if (dto.getReceiverName() != null) addr.setReceiverName(dto.getReceiverName());
        if (dto.getReceiverPhone() != null) addr.setReceiverPhone(dto.getReceiverPhone());
        if (dto.getProvince() != null) addr.setProvince(dto.getProvince());
        if (dto.getCity() != null) addr.setCity(dto.getCity());
        if (dto.getDistrict() != null) addr.setDistrict(dto.getDistrict());
        if (dto.getDetailAddress() != null) addr.setDetailAddress(dto.getDetailAddress());
        if (dto.getIsDefault() != null) addr.setIsDefault(dto.getIsDefault());
    }
}
