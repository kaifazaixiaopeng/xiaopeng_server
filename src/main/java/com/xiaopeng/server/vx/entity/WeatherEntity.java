package com.xiaopeng.server.vx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName: WeatherEntity
 * @Author: BUG-WZP
 * @Since: 2023/5/16
 * @Remark:
 */
@Data
@TableName("weather")
public class WeatherEntity {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @TableField("fxDate")
    private String fxDate;

    @TableField("tempMax")
    private String tempMax;

    @TableField("tempMin")
    private String tempMin;

    @TableField("textDay")
    private String textDay;

    @TableField("windDirDay")
    private String windDirDay;

    @TableField("windSpeedDay")
    private String windSpeedDay;

    @TableField("fxLink")
    private String fxLink;

    @TableField("updateTime")
    private Date updateTime;

    @TableField("city")
    private String city;
}
