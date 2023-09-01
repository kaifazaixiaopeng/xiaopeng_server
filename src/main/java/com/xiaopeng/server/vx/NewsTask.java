package com.xiaopeng.server.vx;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaopeng.server.app.bean.utils.HttpUtil;
import com.xiaopeng.server.vx.entity.Message;
import com.xiaopeng.server.vx.entity.NewsEntity;
import com.xiaopeng.server.vx.entity.WeatherEntity;
import com.xiaopeng.server.vx.service.NewsService;
import com.xiaopeng.server.vx.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @ClassName: News
 * @Author: BUG-WZP
 * @Since: 2023/6/1
 * @Remark:
 */
@Configuration
@Slf4j
public class NewsTask {

    @Autowired
    private NewsService newsService;
    private String content;

//    @Autowired
//    private LogService logService;
    //"0 0 2 ? * *"
    @Scheduled(fixedDelay =300000 )
    public void grabBaiduHotNewsJson() {
        log.info("====================================");
//        LogEntity logEntity = new LogEntity();
//        logEntity.setContent("热搜爬虫task开始");
//        logEntity.setIsSuccess(1);
//        logEntity.setCreateTime(new Date());
//        logService.save(logEntity);
        String url = "https://top.baidu.com/board?tab=realtime&sa=fyb_realtime_31065";
        List<NewsEntity> list = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();
            //标题
            Elements titles = doc.select(".c-single-text-ellipsis");
            //图片
            Elements imgs = doc.select(".category-wrap_iQLoo .index_1Ew5p").next("img");
            //内容
            Elements contents = doc.select(".hot-desc_1m_jR.large_nSuFU");
            //推荐图
            Elements urls = doc.select(".category-wrap_iQLoo a.img-wrapper_29V76");
            //热搜指数
            Elements levels = doc.select(".hot-index_1Bl1a");
            for (int i = 0; i < levels.size(); i++) {
                NewsEntity o = new NewsEntity();
                o.setTitle(titles.get(i).text().trim());
                o.setContent(contents.get(i).text().replaceAll("查看更多>", "").trim());
                log.info(titles.get(i).text().trim() + " : " + contents.get(i).text().replaceAll("查看更多>", "").trim());
                o.setImg(imgs.get(i).attr("src"));
                o.setUrl(urls.get(i).attr("href"));
                list.add(o);
            }
            List<NewsEntity> res = new ArrayList<>();
            list.forEach(e -> {
                LambdaQueryWrapper<NewsEntity> newsEntityQueryWrapper = new LambdaQueryWrapper<>();
                newsEntityQueryWrapper.eq(NewsEntity::getTitle, e.getTitle());
                NewsEntity one = newsService.getOne(newsEntityQueryWrapper);
                if (ObjectUtil.isEmpty(one)) {
                    res.add(e);
                }
            });

            newsService.saveBatch(res);
//            LogEntity logEntity1 = new LogEntity();
//            logEntity1.setContent("热搜爬虫task结束");
//            logEntity1.setIsSuccess(1);
//            logEntity1.setCreateTime(new Date());
//            logService.save(logEntity1);
            log.info("====================================");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private HttpUtil httpUtil;
    private static final String sendUrl = "http://api01.monyun.cn:7901/sms/v2/std/batch_send";

    /**
     * {
     * "userid": "J10003",
     * "pwd": "26dad7f364507df18f3841cc9c4ff94d",
     * "mobile": "138xxxxxxxx,130xxxxxxxx,180xxxxxxxx",
     * "content": "%d1%e9%d6%a4%c2%eb%a3%ba6666%a3%ac%b4%f2%cb%c0%b6%bc%b2%bb%d2%aa%b8%e6%cb%df%b1%f0%c8%cb%c5%b6%a3%a1",
     * "timestamp": "0803192020",
     * "svrtype": "SMS001",
     * "exno": "0006",
     * "custid": "b3d0a2783d31b21b8573",
     * "exdata": "exdata000002"
     * }
     */
    /**
     * -100001	鉴权不通过,请检查账号,密码,时间戳,固定串,以及MD5算法是否按照文档要求进行设置
     * -100002	用户多次鉴权不通过,请检查帐号,密码,时间戳,固定串,以及MD5算法是否按照文档要求进行设置
     * -100003	用户欠费
     * -100004	custid或者exdata字段填写不合法
     * -100011	短信内容超长
     * -100012	手机号码不合法
     * -100014	手机号码超过最大支持数量（1000）
     * -100029	端口绑定失败
     * -100056	用户账号登录的连接数超限
     * -100057	用户账号登录的IP错误
     * -100999	平台数据库内部错误
     */
    //0 30 18 * * ? *
    @Scheduled(fixedDelay = 300000)
    public void sendWeather() {
        //卷子
        JSONObject jsonObject = new JSONObject();
        String userid = "";
        String pwd = "";
        String mobile = "15207927148";
        String content = getContent();
        String apikey = "";
        jsonObject.put("userid", userid);
        jsonObject.put("pwd", pwd);
        jsonObject.put("mobile", mobile);
        jsonObject.put("apikey", apikey);
        jsonObject.put("content", content);
        Map<String, String> stringStringMap = httpUtil.sendByPost(sendUrl, JSONObject.toJSONString(jsonObject));
        String msg = stringStringMap.get("MSG");
        JSONObject jsonObject1 = JSONObject.parseObject(msg);
        Integer result = jsonObject1.getInteger("result");
        log.info("梦网短信发送失败====》codo:{}",result);
        System.out.println(JSONObject.toJSONString(stringStringMap));
    }

    /**
     * 相同内容群发
     *
     * @param message 参数对象
     * @param msgId   返回值为0，则msgId有值。返回值非0，则msgId为空的字符串。字符串为"手机号码,custId,网关流水号"
     * @return 0:成功 非0:返回错误代码
     * @description
     */
//    public int batchSend(Message message, StringBuffer msgId) {
//        try {
//            // 对短信内容进行编码 urlencode（GBK明文）
//            message.setContent(URLEncodeUtil.encode(message.getContent(), Charset.forName("GBK")));
//            String Message = null;
//            StringBuffer messageBuffer = new StringBuffer("");
//            // 短连接相同内容群发
//            sendSmsByNotKeepAlive("batch_send", message, messageBuffer);
//            // returnInt为0,代表提交成功;returnInt不为0，代表提交失败
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return returnInt;
//    }


    @Autowired
    private WeatherService weatherService;

    public String getContent() {
        Date date = new Date();
        String format = DateUtil.format(DateUtil.tomorrow(), "yyyy-MM-dd");
        System.out.println(format);
        int year = DateUtil.year(date);
        int month = DateUtil.month(date) + 1;
        int day = DateUtil.dayOfMonth(date);
        LambdaQueryWrapper<WeatherEntity> query = new LambdaQueryWrapper<>();
        query.eq(WeatherEntity::getFxDate, format);
        WeatherEntity one = weatherService.getOne(query);
        String s = "尊敬的用户您好，今天是" + year + "年" + month + "月" + day + "日,小鹏气象台为您带来明日天气预报,明日" + one.getTextDay() + ",最高气温" + one.getTempMax() + "℃,最低气温" + one.getTempMin() + "℃," + one.getWindDirDay() + ",风速" + one.getWindSpeedDay() + "级";
        return URLEncodeUtil.encode(s,Charset.forName("GBK"));
    }
//    @Scheduled(fixedDelay = 60000)
//    public void getWeiboHot() {
////        LogEntity logEntity = new LogEntity();
////        logEntity.setContent("微博热搜榜爬取开始");
////        logEntity.setIsSuccess(1);
////        logEntity.setCreateTime(new Date());
////        logService.save(logEntity);
//        String url = "https://s.weibo.com/top/summary?cate=realtimehot";
//        List<NewsEntity> list = new ArrayList<>();
//        try {
//            Document doc = Jsoup.connect(url).get();
//            //标题
//            Elements titles = doc.select("_blank");
//            //图片
//            Elements imgs = doc.select(".index_1Ew5p").next("img");
//            //内容
//            Elements contents = doc.select(".hot-desc_1m_jR.large_nSuFU");
//            //推荐图
//            Elements urls = doc.select(".category-wrap_iQLoo a.img-wrapper_29V76");
//            //热搜指数
//            Elements levels = doc.select(".hot-index_1Bl1a");
//            for (int i = 0; i < levels.size(); i++) {
//                NewsEntity o = new NewsEntity();
//                o.setTitle(titles.get(i).text().trim());
//                o.setContent(contents.get(i).text().replaceAll("查看更多>", "").trim());
//                o.setImg(imgs.get(i).attr("src"));
//                o.setUrl(urls.get(i).attr("href"));
//                list.add(o);
//                log.info(JSONObject.toJSONString(o));
//            }
//            List<NewsEntity> res = new ArrayList<>();
//            list.forEach(e -> {
//                LambdaQueryWrapper<NewsEntity> newsEntityQueryWrapper = new LambdaQueryWrapper<>();
//                newsEntityQueryWrapper.eq(NewsEntity::getTitle, e.getTitle());
//                int count = newsService.count(newsEntityQueryWrapper);
//                if (count == 0) {
//                    res.add(e);
//                }
//            });
//
//            newsService.saveBatch(res);
//            LogEntity logEntity1 = new LogEntity();
//            logEntity1.setContent("热搜爬虫task结束");
//            logEntity1.setIsSuccess(1);
//            logEntity1.setCreateTime(new Date());
//            logService.save(logEntity1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}

