package com.freshtrace.unified.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String phone;
    private String email;
    private Integer gender;
    private Integer status;
    private Integer userType;
    private String realName;
    private BigDecimal balance;
    private List<String> roles;
    private Integer favoriteCount;
    private Integer unpaidOrders;
    private Integer unshippedOrders;
    private Integer unreceivedOrders;
    private LocalDateTime createTime;
}
