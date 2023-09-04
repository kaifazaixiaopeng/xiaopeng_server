package com.xiaopeng.server.vx;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.server.app.bean.utils.HttpUtil;
import com.xiaopeng.server.vx.config.RabbitMqConfig;
import com.xiaopeng.server.vx.entity.DayOFCommemoration;
import com.xiaopeng.server.vx.entity.WeatherEntity;
import com.xiaopeng.server.vx.service.DayOFCommemorationService;
import com.xiaopeng.server.vx.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
@RequestMapping("/timed")
@RestController
public class TimedTask {

//    @Autowired
//    private LogService logService;
    @Autowired
    private WeatherService weatherService;
    private final String key = "e8155303fe134b5ca279eb049cf03138";
    @Autowired
    private HttpUtil httpUtil;
    @Autowired
    private StringRedisTemplate redis;
    @Autowired
    private AccToken accToken;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private DayOFCommemorationService dayOFCommemorationService;

    @Value("${wechatConfig.appId}")
    private String appId;

    public static final String TEMPLATE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";

    //定时获取access_token
    @Scheduled(cron = "0 0 8 * * ?")
    @PostMapping("/getAcc")
    public void getAccessToken() {
        //获取token
        accToken.getAccessTokenMethod();

    }

//    @Scheduled(fixedDelay = 3000)
//    public void mqDemo() {
//        /**
//         * 参数1：交换机名称；参数2：路由键，这里没有使用到路由键，所以为空；参数3：发送的消息内容。
//         */
//        Random random = new Random();
//        int i = random.nextInt();
//        String sb = "我是消息Message" + i;
//        log.info(sb.toString());
//        /**
//         * 普通队列发送消息
//         */
//        rabbitTemplate.convertAndSend(RabbitMqConfig.AMQ_TOPIC, RabbitMqConfig.ROUTING_KEY, sb.toString());
//        /**
//         * 延迟队列发送消息，
//         */
////        rabbitTemplate.convertAndSend(RabbitMqConfig.DELAYED_TOPIC, RabbitMqConfig.DELAYED_KEY, weather, new MessagePostProcessor() {
////            @Override
////            public Message postProcessMessage(Message message) throws AmqpException {
////                //给消息设置延迟毫秒值
////                message.getMessageProperties().setHeader("x-delay", 3000000);
////                return message;
////            }
////        });
//    }
//    @Scheduled(fixedDelay = 300000)
//    public void myTasks() {
//        LogEntity startLog = new LogEntity();
//        startLog.setContent("每日定时任务开启");
//        startLog.setCreateTime(new Date());
//        logService.save(startLog);
//        log.info("每日定时任务开启");
//        String access_token = redis.opsForValue().get("access_token");
//        if (StringUtils.isBlank(access_token)) {
//            AccToken.getAccessTokenMethod();
//        }
//        //查询天气
//        String dateStr = DateUtil.format(new Date(), "yyyy-MM-dd");
//        QueryWrapper<WeatherEntity> query = new QueryWrapper<>();
//        query.eq("fxDate",dateStr);
//        WeatherEntity entity=weatherService.getOne(query);
//        //发送模板消息
//        String content = getContent(entity);
//        sendTextMsg(access_token, content);
//        LogEntity endLog = new LogEntity();
//        endLog.setCreateTime(new Date());
//        endLog.setContent("每日定时任务结束");
//        log.info("每日定时任务结束");
//        logService.save(endLog);
//    rabbitTemplate.convertAndSend(RabbitMqConfig.AMQ_TOPIC,RabbitMqConfig.AMQ_TOPIC,"1111");
//    }

    /**
     * 拼接消息文本
     */
    public String getContent(WeatherEntity entity) {
        String s = "温馨提醒,今天是" + entity.getFxDate() + ",天气" + entity.getTextDay() + "," + entity.getWindDirDay() + ",风速" + entity.getWindSpeedDay() + ",最高气温" + entity.getTempMax()
                + "℃,最低气温" + entity.getTempMin() + "℃";
        if (entity.getTextDay().contains("雨")) {
            s = s + ",请带好雨具";
        }
        return s;
    }

    /***
     * 群发文本消息
     * @param token
     */
    private Integer sendTextMsg(String token, String content) {
        // 接口地址
        String sendMsgApi = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=" + token;
        //整体参数map
        JSONObject paramMap = new JSONObject();
        //相关map
        JSONObject dataMap1 = new JSONObject();
        JSONObject dataMap2 = new JSONObject();
        dataMap1.put("is_to_all", true);//用于设定是否向全部用户发送，值为true或false，选择true该消息群发给所有用户，选择false可根据tag_id发送给指定群组的用户
        dataMap1.put("tag_id", 1);//群发到的标签的tag_id，参见用户管理中用户分组接口，若is_to_all值为true，可不填写tag_id
        dataMap2.put("content", content);//要推送的内容
        paramMap.put("filter", dataMap1);//用于设定图文消息的接收者
        paramMap.put("text", dataMap2);//文本内容
        paramMap.put("msgtype", "text");//群发的消息类型，图文消息为mpnews，文本消息为text，语音为voice，音乐为music，图片为image，视频为video，卡券为wxcard
        Map<String, String> back = httpUtil.sendByPost(sendMsgApi, JSONObject.toJSONString(paramMap));
        String data = back.get("CODE");
        System.out.println("群发返回:" + data);
        JSONObject jsonObject = JSONObject.parseObject(data);
        Integer re = jsonObject.getInteger("errcode");
        return re;

    }

    /**
     * 纪念日
     */
    @Scheduled(fixedDelay = 6000000)
    private void day_of_commemoration() {
        // 指定日期
        LocalDate date = LocalDate.of(2022, 11, 3);
        // 计算天数
        long daysBetween = ChronoUnit.DAYS.between(date, LocalDate.now());
        DayOFCommemoration dayOFCommemoration = new DayOFCommemoration();
        dayOFCommemoration.setId(1L);
        dayOFCommemoration.setNumber(daysBetween);
        dayOFCommemorationService.saveOrUpdate(dayOFCommemoration);
    }

    @Scheduled(fixedDelay = 6000000)
    private void weatherTask() {
//        LogEntity logEntity = new LogEntity();
//        logEntity.setContent("Mq同步天气开始");
//        logEntity.setCreateTime(new Date());
        log.info("Mq同步天气开始");
//        logService.save(logEntity);
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
                    if (ObjectUtil.equal(textDay, textNight)) {
                        weatherEntity.setTextDay(textDay);
                    } else {
                        weatherEntity.setTextDay(textDay + "转" + textNight);
                    }
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
//            LogEntity logEntity1 = new LogEntity();
//            logEntity1.setContent("Mq同步天气结束");
//            logEntity1.setCreateTime(new Date());
            log.info("Mq同步天气结束");
//            logService.save(logEntity1);
            log.info(weather);
        } catch (Exception e) {
//            LogEntity logEntity1 = new LogEntity();
//            logEntity1.setContent("Mq同步天气异常结束"+e.getMessage());
//            logEntity1.setIsSuccess(2);
//            logEntity1.setCreateTime(new Date());
            log.info("Mq同步天气异常结束");
//            logService.save(logEntity1);
        }

    }
    /**
     * 城市搜索
     */
    public String getCity() {
        StringBuilder msg = new StringBuilder();
        //https://geoapi.qweather.com/v2/city/lookup?location=beij&key=YOUR_KEY
        JSONObject city = httpUtil.reqByGet("https://geoapi.qweather.com/v2/city/lookup?location=jiujiang&key=" + key);
        if (ObjectUtil.isEmpty(city)) {
//            LogEntity errorLog = new LogEntity();
//            errorLog.setCreateTime(new Date());
//            errorLog.setContent("获取城市信息失败");
            log.info("获取城市信息失败");
//            logService.save(errorLog);
        }
        JSONObject data = JSONObject.parseObject(city.getString("MSG"));
        JSONArray location = data.getJSONArray("location");
        msg.append(JSONObject.toJSONString(location.get(0)));
        return msg.toString();
    }

    /**
     * 天气
     *
     * @param id --id
     * @return --天气字符串
     */
    public String getWeather(String id) {
        StringBuilder msg = new StringBuilder();
        //https://api.qweather.com/v7/weather/3d?location=101010100&key=YOUR_KEY
        String url = "https://devapi.qweather.com/v7/weather/7d?location=" + id + "&key=" + key;
        JSONObject res = httpUtil.reqByGet(url);
        if (ObjectUtil.isEmpty(res)) {
//            LogEntity errorLog = new LogEntity();
//            errorLog.setCreateTime(new Date());
//            errorLog.setContent("获取天气信息异常");
            log.info("获取天气信息异常");
//            logService.save(errorLog);
        }
        msg.append(res.getString("MSG"));
        String s = msg.toString().replaceAll("\\\\", "");
        log.info(msg.toString());
        return msg.toString();
    }

    /**
     * 微信模板消息，通用
     */
    public void sendPublicMessage(String templateId, String toUserId, Map<String, Object> valueMap) {
        try {
            // 获取access_token
            String accessTokenMethod = accToken.getAccessTokenMethod();
            // 设置模板消息基本参数
            Map<String, Object> map = new HashMap<>();
            map.put("touser", toUserId);
            map.put("template_id", templateId);
            map.put("appid", appId);
            sendMessage(TEMPLATE_URL + accessTokenMethod, map, valueMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息
     *
     * @param url 微信模板消息url
     */
    private void sendMessage(String url, Map<String, Object> map, Map<String, Object> valueMap) throws IllegalAccessException, InstantiationException {
        map.put("data", valueMap);
        String msg = JSON.toJSONString(map);
        JSONObject post = httpUtil.sendpost(url, msg);
        log.info("发送模板消息{}", msg);
        log.info("收到回复{}", post);
//        LogEntity logEntity = new LogEntity();
//        logEntity.setIsSuccess(1);
//        logEntity.setContent(JSONObject.toJSONString(post));
    }


}
