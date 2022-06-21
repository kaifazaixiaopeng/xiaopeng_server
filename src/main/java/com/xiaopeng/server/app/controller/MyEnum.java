package com.xiaopeng.server.app.controller;

/**
 * @Auto:BUGPeng
 * @Date:2022/6/7 16:25
 * @EnumName:MyEnum
 * @Remark:
 */
public enum MyEnum {
    USER(1,"小鹏");
    private int id;
    private String name;
    /**
     * 按照code获得枚举值
     */
    public static MyEnum valueOf(Integer id) {
        if (id != null) {
            for (MyEnum fsEnum : MyEnum.values()) {
                if (fsEnum.getId() == id) {
                    return fsEnum;
                }
            }
        }
        return null;
    }
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    MyEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
