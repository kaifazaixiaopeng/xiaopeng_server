package com.xiaopeng.server.vx;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Auto:BUGPeng
 * @Date:2023/3/30 22:00
 * @ClassName:AccToken
 * @Remark:
 */
@Slf4j
@Component
public class AccToken {

    @Value("${wechatConfig.appId}")
    private  String appId;
    @Value("${wechatConfig.appSecret}")
    private  String appSecret;
    @Autowired
    private  StringRedisTemplate redis;

    /**
     * 获取AccessToken
     *
     * @return
     */
    public  String getAccessTokenMethod() {
        try {
            String token = redis.opsForValue().get("access_token");
            if (StringUtils.isNotBlank(token)) {
                return token;
            } else {
                String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" +
                        appId + "&secret=" + appSecret;
                StringBuilder json = new StringBuilder();
                URL oracle = new URL(url);
                URLConnection yc = oracle.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream(), StandardCharsets.UTF_8));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    json.append(inputLine);
                }
                in.close();
                JSONObject object = (JSONObject) JSONObject.parse(String.valueOf(json));
                log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "获取access_token:{}", object.getString("access_token"));
                if (object.getString("access_token") != null) {
                    String access_token = object.getString("access_token");
                    redis.opsForValue().set("access_token", access_token, 2, TimeUnit.HOURS);
                }
                return object.getString("access_token");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
