package com.xiaopeng.server.app.bean;

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
    private int id;
    private String name;
}
