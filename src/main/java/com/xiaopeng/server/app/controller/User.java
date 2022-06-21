package com.xiaopeng.server.app.controller;

import lombok.Data;

import java.io.Serializable;

/**
 * @Auto:BUGPeng
 * @Date:2022/5/31 16:21
 * @ClassName:user
 * @Remark:
 */
@Data
public class User implements Serializable {
    private String name;
    private int id;
}
