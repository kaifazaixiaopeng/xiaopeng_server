package com.xiaopeng.server.vx;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.server.app.bean.utils.HttpUtil;
import com.xiaopeng.server.vx.entity.LogEntity;
import com.xiaopeng.server.vx.mapper.LogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @ClassName: TimedTesk
 * @Author: BUG-WZP
 * @Since: 2023/5/15
 * @Remark:
 */
@Configuration
@Slf4j
public class TimedTesk {

    @Autowired
    private LogMapper logMapper;
    private final String key = "e8155303fe134b5ca279eb049cf03138";

    @Autowired
    private HttpUtil httpUtil;

    @Scheduled(cron = "0 0 8 * * *")
    public void myTasks() {
        LogEntity startLog = new LogEntity();
        startLog.setContent("每日定时任务开启");
        logMapper.insert(startLog);
        log.info("每日定时任务开启");
        String weather = getWeather();
        //调用wechart api获取accessToken

        //发送模板消息

        LogEntity endLog = new LogEntity();
        endLog.setContent("每日定时任务结束");
        log.info("每日定时任务结束");
        logMapper.insert(endLog);
    }

//    @Scheduled(fixedDelay = 1000)
//    private void testTasks() {
//        String now = DateUtil.now();
//        log.info(now);
//    }

    public String getWeather() {
        StringBuilder msg = new StringBuilder();
        try {
            //https://geoapi.qweather.com/v2/city/lookup?location=beij&key=YOUR_KEY
            JSONObject city = httpUtil.reqByGet("https://geoapi.qweather.com/v2/city/lookup?location=jiujiang&key=" + key);
            if (ObjectUtil.isEmpty(city)) {
                LogEntity errorLog = new LogEntity();
                errorLog.setContent("获取城市信息失败");
                log.info("获取城市信息失败");
                logMapper.insert(errorLog);
            }
            JSONObject data = JSONObject.parseObject(city.getString("MSG"));
            JSONArray location = data.getJSONArray("location");
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(location.get(0)));
            String id = jsonObject.getString("id");
            //https://api.qweather.com/v7/weather/3d?location=101010100&key=YOUR_KEY
            String url = "https://devapi.qweather.com/v7/weather/3d?location=" + id + "&key=" + key;
            JSONObject res = httpUtil.reqByGet(url);
            if(ObjectUtil.isEmpty(res)){
                LogEntity errorLog = new LogEntity();
                errorLog.setContent("获取天气信息异常");
                log.info("获取天气信息异常");
                logMapper.insert(errorLog);
            }
            msg.append(res.getString("MSG"));
            msg.toString().replaceAll("\\\\", "");
            log.info(msg.toString());
        } catch (Exception e) {
            msg.append(e.getMessage());
        }
        return msg.toString();
    }

}
