package com.xiaopeng.server.vx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaopeng.server.vx.entity.WeatherEntity;
import com.xiaopeng.server.vx.mapper.WeatherMapper;
import com.xiaopeng.server.vx.service.WeatherService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: WeatherServiceImpl
 * @Author: BUG-WZP
 * @Since: 2023/5/16
 * @Remark:
 */
@Service
public class WeatherServiceImpl extends ServiceImpl<WeatherMapper, WeatherEntity> implements WeatherService {
}
