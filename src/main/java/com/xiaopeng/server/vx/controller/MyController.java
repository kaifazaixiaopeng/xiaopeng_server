package com.xiaopeng.server.vx.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.server.app.bean.common.ResultBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.util.List;
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
     */
    @GetMapping("/createCaptcha")
    public ResultBean createCaptcha(HttpServletRequest request, HttpServletResponse response) {
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(130, 48);
        lineCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));
        String verCode = lineCaptcha.getCode();
        log.info("=====>{}",verCode);
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

}
