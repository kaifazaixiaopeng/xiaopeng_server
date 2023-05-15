package com.xiaopeng.server.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Auto:BUGPeng
 * @Date:2022/11/9 23:24
 * @ClassName:AiXincontroller
 * @Remark:
 */
@Controller
@Slf4j
public class AiXincontroller {
    @RequestMapping("/index")
    public String show() {
        log.info("进来了");
        return "aixin";
    }

}