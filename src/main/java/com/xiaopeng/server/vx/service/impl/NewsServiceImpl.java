package com.xiaopeng.server.vx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaopeng.server.vx.entity.NewsEntity;
import com.xiaopeng.server.vx.mapper.NewsMapper;
import com.xiaopeng.server.vx.service.NewsService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: NewsServiceImpl
 * @Author: BUG-WZP
 * @Since: 2023/6/6
 * @Remark:
 */
@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, NewsEntity> implements NewsService {
}
