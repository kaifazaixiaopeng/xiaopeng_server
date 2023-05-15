package com.xiaopeng.server.vx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaopeng.server.vx.entity.LogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName: logMapper
 * @Author: BUG-WZP
 * @Since: 2023/5/15
 * @Remark:
 */
@Mapper
public interface LogMapper extends BaseMapper<LogEntity> {
}
