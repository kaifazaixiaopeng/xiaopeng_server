package com.xiaopeng.server.vx;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.server.app.bean.utils.HttpUtil;
import com.xiaopeng.server.vx.entity.LogEntity;
import com.xiaopeng.server.vx.entity.WeatherEntity;
import com.xiaopeng.server.vx.mapper.LogMapper;
import com.xiaopeng.server.vx.mapper.WeatherMapper;
import com.xiaopeng.server.vx.service.LogService;
import com.xiaopeng.server.vx.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private LogService logService;
    @Autowired
    private WeatherService weatherService;
    private final String key = "e8155303fe134b5ca279eb049cf03138";

    @Autowired
    private HttpUtil httpUtil;

    @Scheduled(cron = "0 0 8 * * ?")
    public void myTasks() {
        LogEntity startLog = new LogEntity();
        startLog.setContent("每日定时任务开启");
        logService.save(startLog);
        log.info("每日定时任务开启");
        String city = getCity();
        JSONObject js = JSONObject.parseObject(city);
        String weather = getWeather(js.getString("id"));
        JSONObject jsonObject = JSONObject.parseObject(weather);
        //调用wechart api获取accessToken

        //发送模板消息

        LogEntity endLog = new LogEntity();
        endLog.setContent("每日定时任务结束");
        log.info("每日定时任务结束");
        logService.save(endLog);
    }

    @Scheduled(cron = "0 0 0/2 * * ?")
    private void testTasks() {
        LogEntity logEntity = new LogEntity();
        logEntity.setContent("天气定时任务开始");
        log.info("天气定时任务开始");
        logService.save(logEntity);
        try {
            String city = getCity();
            JSONObject js = JSONObject.parseObject(city);
            String name = js.getString("name");
            String weather = getWeather(js.getString("id"));
            JSONObject jsonObject = JSONObject.parseObject(weather);
            //更新时间
            Date updateTime = new Date();
            String time = jsonObject.getString("updateTime");
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmXXX");
            try {
                updateTime = inputFormat.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //链接地址
            String fxLink = jsonObject.getString("fxLink");
            JSONArray daily = jsonObject.getJSONArray("daily");
            if (CollectionUtil.isNotEmpty(daily)) {
                Date finalUpdateTime = updateTime;
                List<WeatherEntity> collect = daily.stream().map(e -> {
                    JSONObject day = JSONObject.parseObject(JSONObject.toJSONString(e));
                    WeatherEntity weatherEntity = new WeatherEntity();
                    //天气日期
                    weatherEntity.setFxDate(day.getString("fxDate"));
                    //最大气温
                    weatherEntity.setTempMax(day.getString("tempMax"));
                    //最小气温
                    weatherEntity.setTempMin(day.getString("tempMin"));
                    //天气：多云
                    String textDay = day.getString("textDay");
                    String textNight = day.getString("textNight");
                    weatherEntity.setTextDay(textDay+"转"+textNight);
                    //风向
                    weatherEntity.setWindDirDay(day.getString("windDirDay"));
                    //风速
                    weatherEntity.setWindSpeedDay(day.getString("windSpeedDay"));
                    weatherEntity.setFxLink(fxLink);
                    weatherEntity.setCity(name);
                    weatherEntity.setUpdateTime(finalUpdateTime);
                    return weatherEntity;
                }).collect(Collectors.toList());
                List<WeatherEntity> weatherEntities = weatherService.list();
                if (CollectionUtil.isEmpty(weatherEntities)) {
                    weatherService.saveBatch(collect);
                } else {
                    Map<String, WeatherEntity> map = weatherEntities.stream().collect(Collectors.toMap(WeatherEntity::getFxDate, Function.identity()));
                    List<WeatherEntity> insertList = collect.stream().filter(e -> ObjectUtil.isEmpty(map.get(e.getFxDate()))).collect(Collectors.toList());
                    List<WeatherEntity> updateList = collect.stream().filter(e -> ObjectUtil.isNotEmpty(map.get(e.getFxDate()))).peek(e -> {
                        WeatherEntity weatherEntity = map.get(e.getFxDate());
                        e.setId(weatherEntity.getId());
                    }).collect(Collectors.toList());
                    weatherService.saveBatch(insertList);
                    weatherService.updateBatchById(updateList);
                }
            }
            LogEntity logEntity1 = new LogEntity();
            logEntity1.setContent("天气定时任务结束");
            log.info("天气定时任务结束");
            logService.save(logEntity1);
            log.info(weather);
        } catch (Exception e) {
            LogEntity logEntity1 = new LogEntity();
            logEntity1.setContent("天气定时任务异常结束");
            logEntity1.setIsSuccess(2);
            log.info("天气定时任务异常结束");
            logService.save(logEntity1);
        }

    }

    /**
     * 城市搜索
     *
     * @return
     */
    public String getCity() {
        StringBuilder msg = new StringBuilder();
        //https://geoapi.qweather.com/v2/city/lookup?location=beij&key=YOUR_KEY
        JSONObject city = httpUtil.reqByGet("https://geoapi.qweather.com/v2/city/lookup?location=jiujiang&key=" + key);
        if (ObjectUtil.isEmpty(city)) {
            LogEntity errorLog = new LogEntity();
            errorLog.setContent("获取城市信息失败");
            log.info("获取城市信息失败");
            logService.save(errorLog);
        }
        JSONObject data = JSONObject.parseObject(city.getString("MSG"));
        JSONArray location = data.getJSONArray("location");
        msg.append(JSONObject.toJSONString(location.get(0)));
        return msg.toString();
    }

    /**
     * 天气
     *
     * @param id
     * @return
     */
    public String getWeather(String id) {
        StringBuilder msg = new StringBuilder();
        //https://api.qweather.com/v7/weather/3d?location=101010100&key=YOUR_KEY
        String url = "https://devapi.qweather.com/v7/weather/7d?location=" + id + "&key=" + key;
        JSONObject res = httpUtil.reqByGet(url);
        if (ObjectUtil.isEmpty(res)) {
            LogEntity errorLog = new LogEntity();
            errorLog.setContent("获取天气信息异常");
            log.info("获取天气信息异常");
            logService.save(errorLog);
        }
        msg.append(res.getString("MSG"));
        msg.toString().replaceAll("\\\\", "");
        log.info(msg.toString());
        return msg.toString();
    }

}
