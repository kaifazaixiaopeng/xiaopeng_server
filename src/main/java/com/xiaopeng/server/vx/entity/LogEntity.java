package com.xiaopeng.server.vx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @ClassName: LogEntity
 * @Author: BUG-WZP
 * @Since: 2023/5/15
 * @Remark:
 */
@TableName("logger")
@Data
public class LogEntity {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @TableField("content")
    private String content;

}