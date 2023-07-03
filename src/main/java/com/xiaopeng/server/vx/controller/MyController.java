package com.xiaopeng.server.vx.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.server.app.bean.common.ResultBean;
import com.xiaopeng.server.vx.config.AutoLog;
import com.xiaopeng.server.vx.utils.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: RestController
 * @Author: BUG-WZP
 * @Since: 2023/6/9
 * @Remark:
 */
@RestController
@RequestMapping("/demo")
@Slf4j
public class MyController {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * TODO 图形验证码
     * 验证完成验证码后需要删除验证码
     *              redisTemplate.delete("xiaopeng:server:" + verCode);
     */
    @GetMapping("/createCaptcha")
    public ResultBean createCaptcha(HttpServletRequest request, HttpServletResponse response) {
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(130, 48,5,0);
        lineCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));
        String verCode = lineCaptcha.getCode();
        log.info("验证码=====>{}",verCode);
        try {
            redisTemplate.opsForValue().set("xiaopeng:server:" + verCode, verCode, 10L, TimeUnit.MINUTES);
            lineCaptcha.write(response.getOutputStream());
            response.getOutputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultBean.fail("fail",e.getMessage());
        }
        return ResultBean.of(200,"success",null);
    }

    @PostMapping("/delete")
    public void delete(@RequestParam("ids") List<Integer> ids){
        System.out.println(JSONObject.toJSONString(ids));
    }

    /**
     * 解密:
     *      继承HttpServletResponseWrapper 类去重写getParameterValues，getParameter等方法
     * 加密:
     *      继承HttpServletRequestWrapper类，supports()方法返回值决定了beforeBodyRead()方法是否需要执行
     * @param
     */
    @GetMapping("/demoAOPRSA")
    @AutoLog
    public void demoAOPRSA(@RequestParam("userName")String userName){
        System.out.println("进来了");
        System.out.println("解密后的参数===》"+JSONObject.toJSONString(userName));
    }

    @GetMapping("/testRSA")
    public void testRSA(){
        System.out.println("testRSA");
        //加密参数
        reqByGet("http://localhost:8080/api/demo/demoAOPRSA?userName=admin");
    }
    public JSONObject reqByGet(String url) {
        JSONObject jsonObject = new JSONObject();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        //url截取，获取所有参数
        String[] parts = url.split("\\?");
        url=parts[0];
        String params = parts[1];
        //进行公钥加密
        //获取公钥私钥
        Map<Integer, String> keyMap = null;
        String publicKey;
        String privateKey;
        try {
            keyMap = (Map<Integer, String>) RSAUtil.genKeyPair();
            publicKey = keyMap.get(0);
            System.out.println("公钥:" + publicKey);
            privateKey = keyMap.get(1);
            System.out.println("私钥:" + privateKey);
            String encrypt = RSAUtil.encrypt(params, publicKey);
            url=url+"?cip="+encrypt;
            String resultString = "";
            log.info("新url====》{}",url);
            try {
                HttpGet httpGet = new HttpGet(url);
                response = httpclient.execute(httpGet);
                if (response.getEntity() != null) {
                    resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
//                resultMap.put("responseEntity", resultString);
//                resultMap.put("responseEntityGB2312", EntityUtils.toString(response.getEntity(), "GB2312"));
                }
                jsonObject.put("CODE", 200);
                jsonObject.put("MSG", resultString);
            } catch (Exception e) {
                log.error("===》HttpUtil.get方法执行出错，url：{}，信息：{}", url, e.toString());
                jsonObject.put("CODE", 500);
                jsonObject.put("MSG", e.toString());
            } finally {
                try {
                    if (response != null) {
                        response.close();
                    }
                    httpclient.close();
                } catch (IOException e) {
                    log.error("===》HttpUtil.get方法关闭出错，url：{}，信息：{}", url, e.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
