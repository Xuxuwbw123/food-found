package com.freshtrace.unified.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.freshtrace.unified.dto.ResetPasswordDTO;
import com.freshtrace.unified.dto.UserProfileDTO;
import com.freshtrace.unified.dto.UserVO;
import com.freshtrace.unified.entity.User;

public interface UserService extends IService<User> {
    UserVO getProfile();
    void updateProfile(UserProfileDTO dto);
    void resetPassword(ResetPasswordDTO dto);
}
