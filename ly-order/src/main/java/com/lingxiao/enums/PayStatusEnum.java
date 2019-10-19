package com.lingxiao.enums;

public enum  PayStatusEnum {
    NOT_PAY(0),
    SUCCESS(0),
    FAIL(0),
    ;
    int value;

    PayStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
