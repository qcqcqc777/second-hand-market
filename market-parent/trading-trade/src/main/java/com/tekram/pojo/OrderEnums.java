package com.tekram.pojo;

public class OrderEnums {
    // 查询角色
    public enum Role {
        BUYER,
        SELLER
    }

    // 订单状态
    public enum Status {
        WAIT_PAY(0),
        WAIT_SHIP(1),
        WAIT_RECEIVE(2),
        COMPLETED(3),
        CANCELLED(4);

        private final int code;

        Status(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}

