package com.freshtrace.unified.dto;

import lombok.Data;

@Data
public class AddressDTO {
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private Integer isDefault;
}
