package com.xiaopeng.server.vx.service;

import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.server.vx.entity.NewsEntity;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @Scheduled(cron = "0 0 0/1  * * ? ")
    public void grabBaiduHotNewsJson() {
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
//                o.setImg(imgs.get(i).attr("src"));
//                o.setUrl(urls.get(i).attr("href"));
                list.add(o);
                log.info(JSONObject.toJSONString(o));
            }
            newsService.saveBatch(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
