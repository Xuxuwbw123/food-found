package com.freshtrace.unified.controller;

import com.freshtrace.unified.common.Result;
import com.freshtrace.unified.dto.ResetPasswordDTO;
import com.freshtrace.unified.dto.UserProfileDTO;
import com.freshtrace.unified.dto.UserVO;
import com.freshtrace.unified.entity.SysUser;
import com.freshtrace.unified.service.AuthService;
import com.freshtrace.unified.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/user", "/api/user"})
public class UserController {

    @Autowired private UserService userService;
    @Autowired private AuthService authService;

    @GetMapping("/info")
    public Result<UserVO> getInfo() {
        try { return Result.ok(userService.getProfile()); }
        catch (Exception e) { return Result.fail(e.getMessage()); }
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UserProfileDTO dto) {
        try { userService.updateProfile(dto); return Result.ok(); }
        catch (Exception e) { return Result.fail(e.getMessage()); }
    }

    @PutMapping("/password")
    public Result<Void> resetPassword(@RequestBody ResetPasswordDTO dto) {
        try { userService.resetPassword(dto); return Result.ok(); }
        catch (Exception e) { return Result.fail(e.getMessage()); }
    }
}
