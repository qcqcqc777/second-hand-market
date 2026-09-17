package com.tekram.pojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressVO {
    private String id;
    private String consigneeName;
    private String consigneePhone;
    private String provinceName;
    private String cityName;
    private String regionName;
    private String detailAddress;
    private boolean defaultFlag;
}
