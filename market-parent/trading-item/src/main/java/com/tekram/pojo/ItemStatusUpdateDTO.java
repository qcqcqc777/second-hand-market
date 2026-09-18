package com.tekram.pojo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
public class ItemStatusUpdateDTO {
    @NotNull(message = "状态不能为空")
    private ItemStatus status;
    @NotNull(message = "版本号不能为空")
    @Min(value = 0, message = "版本号不能小于0")
    private Integer version;

    @Getter
    public enum ItemStatus {
        ON_SALE((byte) 1),
        OFF_SHELF((byte) 2);

        private final Byte code;
        ItemStatus(Byte code) {
            this.code = code;
        }
    }

}
