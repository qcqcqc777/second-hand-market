package com.tekram.pojo;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.tekram.exception.UnknownFieldException;

@Data
public class AddressDTO {
    private String consigneeName;
    private String consigneePhone;
    private String provinceName;
    private String cityName;
    private String regionName;
    private String detailAddress;
    private Boolean defaultFlag;

    @JsonAnySetter
    public void rejectUnknownField(String name, Object value) {
        throw new UnknownFieldException(name);
    }
}
