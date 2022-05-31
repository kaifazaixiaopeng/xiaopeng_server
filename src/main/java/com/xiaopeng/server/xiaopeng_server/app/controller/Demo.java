package com.xiaopeng.server.xiaopeng_server.app.controller;

import com.xiaopeng.server.xiaopeng_server.app.bean.ResultBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auto:BUGPeng
 * @Date:2022/4/2816:48
 * @ClassName:Demo
 * @Remark:
 */

@Slf4j
@RestController
@RequestMapping("/api/v1/demo")
public class Demo {
    @PostMapping("/hellowWord")
    public ResultBean testxiaopeng(Model model){
        model.addAttribute("hi", "小鹏主机的第一个程序!");
        return new ResultBean(ResultBean.SUCCESS,"Hello,小鹏!");
    }
}



