package com.freshtrace.unified.service;

import com.freshtrace.unified.dto.LoginDTO;
import com.freshtrace.unified.dto.RegisterDTO;
import com.freshtrace.unified.entity.SysUser;

import java.util.Map;

public interface AuthService {
    Map<String, Object> login(LoginDTO dto);
    SysUser register(RegisterDTO dto);
    void logout();
    SysUser getCurrentUser();
}
