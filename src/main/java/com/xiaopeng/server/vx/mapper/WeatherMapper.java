package com.xiaopeng.server.vx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaopeng.server.vx.entity.WeatherEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName: WeatherMapper
 * @Author: BUG-WZP
 * @Since: 2023/5/16
 * @Remark:
 */
@Mapper
public interface WeatherMapper extends BaseMapper<WeatherEntity> {
}
