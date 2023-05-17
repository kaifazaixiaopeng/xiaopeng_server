package com.xiaopeng.server.vx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @ClassName: User
 * @Author: BUG-WZP
 * @Since: 2023/5/16
 * @Remark:
 */
@TableName("user")
@Data
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("text")
    private String text;

}
