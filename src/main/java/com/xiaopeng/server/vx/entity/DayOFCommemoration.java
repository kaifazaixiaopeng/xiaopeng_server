package com.xiaopeng.server.vx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @ClassName: DayOFCommemoration
 * @Author: BUG-WZP
 * @Since: 2023/5/22
 * @Remark:
 */
@Data
@TableName("day_of_commemoration")
public class DayOFCommemoration {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    @TableField("number")
    private Long number;
}
