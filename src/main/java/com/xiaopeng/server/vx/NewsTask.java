package com.xiaopeng.server.vx;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaopeng.server.vx.entity.LogEntity;
import com.xiaopeng.server.vx.entity.NewsEntity;
import com.xiaopeng.server.vx.service.LogService;
import com.xiaopeng.server.vx.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Autowired
    private LogService logService;

    @Scheduled(fixedDelay = 120000)
    public void grabBaiduHotNewsJson() {
        log.info("====================================");
        LogEntity logEntity = new LogEntity();
        logEntity.setContent("热搜爬虫task开始");
        logEntity.setIsSuccess(1);
        logEntity.setCreateTime(new Date());
        logService.save(logEntity);
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
                log.info(titles.get(i).text().trim()+" : "+contents.get(i).text().replaceAll("查看更多>", "").trim());
                o.setImg(imgs.get(i).attr("src"));
                o.setUrl(urls.get(i).attr("href"));
                list.add(o);
            }
            List<NewsEntity> res = new ArrayList<>();
            list.forEach(e -> {
                LambdaQueryWrapper<NewsEntity> newsEntityQueryWrapper = new LambdaQueryWrapper<>();
                newsEntityQueryWrapper.eq(NewsEntity::getTitle, e.getTitle());
                int count = newsService.count(newsEntityQueryWrapper);
                if (count == 0) {
                    res.add(e);
                }
            });

            newsService.saveBatch(res);
            LogEntity logEntity1 = new LogEntity();
            logEntity1.setContent("热搜爬虫task结束");
            logEntity1.setIsSuccess(1);
            logEntity1.setCreateTime(new Date());
            logService.save(logEntity1);
            log.info("====================================");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

