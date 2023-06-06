package com.xiaopeng.server.vx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaopeng.server.vx.entity.NewsEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName: NewsMapper
 * @Author: BUG-WZP
 * @Since: 2023/6/6
 * @Remark:
 */
@Mapper
public interface NewsMapper extends BaseMapper<NewsEntity> {
}
