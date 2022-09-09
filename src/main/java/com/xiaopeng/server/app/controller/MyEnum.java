package com.xiaopeng.server.app.controller;

/**
 * @Auto:BUGPeng
 * @Date:2022/6/7 16:25
 * @EnumName:MyEnum
 * @Remark:
 */
public enum MyEnum {
    USER_3("psn_1","小烁"),
    USER_2("psn_2","小柚"),
    USER_1("psn_3","小鹏");
    private String code;
    private String name;
    /**
     * 按照code获得枚举值
     */
    public static MyEnum getFromCode(String code) {
        if (code != null) {
            for (MyEnum fsEnum : MyEnum.values()) {
                if (fsEnum.getCode() .equals( code)) {
                    return fsEnum;
                }
            }
        }
        return null;
    }
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    MyEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
