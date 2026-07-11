package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshtrace.unified.common.UserContext;
import com.freshtrace.unified.dto.ResetPasswordDTO;
import com.freshtrace.unified.dto.UserProfileDTO;
import com.freshtrace.unified.dto.UserVO;
import com.freshtrace.unified.entity.*;
import com.freshtrace.unified.mapper.*;
import com.freshtrace.unified.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired private SysUserRoleMapper sysUserRoleMapper;
    @Autowired private SysRoleMapper sysRoleMapper;
    @Autowired private ProductFavoriteMapper productFavoriteMapper;
    @Autowired private OrderInfoMapper orderInfoMapper;

    @Override
    public UserVO getProfile() {
        User user = baseMapper.selectById(UserContext.getUserId());
        if (user == null) throw new RuntimeException("user not found");
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);

        var urList = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, user.getId()));
        var roleIds = urList.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
        if (!roleIds.isEmpty()) {
            var roles = sysRoleMapper.selectBatchIds(roleIds);
            vo.setRoles(roles.stream().map(SysRole::getRoleName).collect(Collectors.toList()));
        }
        vo.setFavoriteCount(productFavoriteMapper.selectCount(
                new LambdaQueryWrapper<ProductFavorite>().eq(ProductFavorite::getUserId, user.getId())).intValue());
        vo.setUnpaidOrders(orderInfoMapper.selectCount(
                new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getUserId, user.getId()).eq(OrderInfo::getOrderStatus, 0)).intValue());
        vo.setUnshippedOrders(orderInfoMapper.selectCount(
                new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getUserId, user.getId()).eq(OrderInfo::getOrderStatus, 1)).intValue());
        vo.setUnreceivedOrders(orderInfoMapper.selectCount(
                new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getUserId, user.getId()).eq(OrderInfo::getOrderStatus, 2)).intValue());
        return vo;
    }

    @Override
    public void updateProfile(UserProfileDTO dto) {
        User user = baseMapper.selectById(UserContext.getUserId());
        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getAvatar() != null) user.setAvatar(dto.getAvatar());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getGender() != null) user.setGender(dto.getGender());
        baseMapper.updateById(user);
    }

    @Override
    public void resetPassword(ResetPasswordDTO dto) {
        User user = baseMapper.selectById(UserContext.getUserId());
        if (user == null) throw new RuntimeException("user not found");
        // Support both BCrypt and plain text passwords
        boolean match = false;
        if (user.getPassword() != null && user.getPassword().startsWith("$2")) {
            match = cn.hutool.crypto.digest.BCrypt.checkpw(dto.getOldPassword(), user.getPassword());
        } else {
            match = user.getPassword().equals(dto.getOldPassword());
        }
        if (!match) throw new RuntimeException("old password is wrong");
        user.setPassword(cn.hutool.crypto.digest.BCrypt.hashpw(dto.getNewPassword(), cn.hutool.crypto.digest.BCrypt.gensalt()));
        baseMapper.updateById(user);
    }
}
