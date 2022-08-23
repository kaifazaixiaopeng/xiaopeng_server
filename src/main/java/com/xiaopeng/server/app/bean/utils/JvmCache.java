package com.xiaopeng.server.app.bean.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Auto:BUGPeng
 * @Date:2022/7/12 9:19
 * @ClassName:JvmCache
 * @Remark:
 */
public class JvmCache {
    private  static  volatile List list=new ArrayList<Integer>();
    private static final ReentrantLock lock=new ReentrantLock();
    public static void main(String[] args) {
        try {
            lock.lock();
            list.add(1);
            System.out.println(JSONObject.toJSONString(list));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}
