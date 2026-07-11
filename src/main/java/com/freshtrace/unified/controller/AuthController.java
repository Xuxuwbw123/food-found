package com.freshtrace.unified.controller;

import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.config.JwtUtil;
import com.freshtrace.unified.dto.LoginDTO;
import com.freshtrace.unified.dto.RegisterDTO;
import com.freshtrace.unified.entity.SysUser;
import com.freshtrace.unified.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private AuthService authService;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto) {
        try { return Result.ok(authService.login(dto)); }
        catch (Exception e) { return Result.fail(e.getMessage()); }
    }

    @PostMapping("/register")
    public Result<SysUser> register(@RequestBody RegisterDTO dto) {
        try { return Result.ok(authService.register(dto)); }
        catch (Exception e) { return Result.fail(e.getMessage()); }
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout(); return Result.ok();
    }

    @PostMapping("/refresh")
    public Result<Map<String, String>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            return Result.fail(401, "Token 无效");
        }
        Long userId = jwtUtil.getUserId(refreshToken);
        Integer userType = jwtUtil.getUserType(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(userId, userType);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId, userType);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);
        return Result.ok(tokens);
    }
}
