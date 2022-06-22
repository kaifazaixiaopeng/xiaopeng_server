package com.xiaopeng.server.app.mapper;

import com.xiaopeng.server.app.bean.annotation.MyBatisRepository;
import com.xiaopeng.server.app.bean.pojo.User;

import java.util.List;

/**
 * @Auto:BUGPeng
 * @Date:2022/6/10 10:03
 * @ClassName:DemoMapper
 * @Remark:
 */
@MyBatisRepository
//@RespectBinding
public interface DemoMapper {
     List<User> getAllData();
}