package com.freshtrace.unified.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshtrace.unified.common.UserContext;
import com.freshtrace.unified.config.JwtUtil;
import com.freshtrace.unified.dto.LoginDTO;
import com.freshtrace.unified.dto.RegisterDTO;
import com.freshtrace.unified.entity.SysUser;
import com.freshtrace.unified.mapper.SysUserMapper;
import com.freshtrace.unified.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired private SysUserMapper sysUserMapper;
    @Autowired private JwtUtil jwtUtil;

    @Override
    public Map<String, Object> login(LoginDTO dto) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (user == null || user.getStatus() == 0) {
            throw new RuntimeException("账号不存在或已禁用");
        }
        // 支持 BCrypt 加密密码和明文密码
        boolean passwordMatch = false;
        if (user.getPassword() != null && user.getPassword().startsWith("$2")) {
            passwordMatch = cn.hutool.crypto.digest.BCrypt.checkpw(dto.getPassword(), user.getPassword());
        } else {
            passwordMatch = dto.getPassword().equals(user.getPassword());
        }
        if (!passwordMatch) {
            throw new RuntimeException("用户名或密码错误");
        }
        user.setLastLoginTime(LocalDateTime.now());
        sysUserMapper.updateById(user);

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUserType());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUserType());

        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        userMap.put("nickname", user.getNickname());
        userMap.put("user_type", user.getUserType());
        userMap.put("avatar", user.getAvatar());
        result.put("user", userMap);
        return result;
    }

    @Override
    public SysUser register(RegisterDTO dto) {
        Long count = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new RuntimeException("用户名已被使用");
        }
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(cn.hutool.crypto.digest.BCrypt.hashpw(dto.getPassword(), cn.hutool.crypto.digest.BCrypt.gensalt()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setStatus(1);
        user.setUserType(1);
        user.setGender(0);
        user.setBalance(BigDecimal.ZERO);
        user.setCreateTime(LocalDateTime.now());
        sysUserMapper.insert(user);
        return user;
    }

    @Override
    public void logout() {
    }

    @Override
    public SysUser getCurrentUser() {
        return sysUserMapper.selectById(UserContext.getUserId());
    }
}
