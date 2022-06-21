package com.xiaopeng.server.app.dao;

import com.xiaopeng.server.app.bean.MyBatisRepository;
import com.xiaopeng.server.app.bean.User;

import java.util.List;

/**
 * @Auto:BUGPeng
 * @Date:2022/6/10 10:03
 * @ClassName:DemoMapper
 * @Remark:
 */
@MyBatisRepository
public interface DemoMapper {
     List<User> getAllData();
}
