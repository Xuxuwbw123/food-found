package com.freshtrace.unified.dto;

import lombok.Data;

@Data
public class UserProfileDTO {
    private String nickname;
    private String avatar;
    private String phone;
    private String email;
    private Integer gender;
}
