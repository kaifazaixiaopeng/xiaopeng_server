package com.xiaopeng.server.vx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaopeng.server.vx.entity.LogEntity;
import com.xiaopeng.server.vx.mapper.LogMapper;
import com.xiaopeng.server.vx.service.LogService;
import com.xiaopeng.server.vx.service.WeatherService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: LogServiceImpl
 * @Author: BUG-WZP
 * @Since: 2023/5/16
 * @Remark:
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, LogEntity> implements LogService {
}
