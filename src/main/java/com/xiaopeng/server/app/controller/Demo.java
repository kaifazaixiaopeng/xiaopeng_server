package com.xiaopeng.server.app.controller;

import com.xiaopeng.server.app.bean.common.ResultBean;
import com.xiaopeng.server.app.bean.pojo.User;
import com.xiaopeng.server.app.mapper.DemoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * @Auto:BUGPeng
 * @Date:2022/4/2816:48
 * @ClassName:Demo
 * @Remark:
 */

@Slf4j
@RestController
@RequestMapping("/v1/demo")
public class Demo {
    @Autowired
    private DemoMapper demoMapper;

    @GetMapping("/hellowWord")
    public ResultBean testxiaopeng(Model model) {
        model.addAttribute("hi", "小鹏主机的第一个程序!");
        return new ResultBean(ResultBean.SUCCESS, "Hello,小鹏!");
    }

    @GetMapping("/bio")
    public File getCSVFile() {
        File file = new File("test.txt");
        return file;
    }
    @GetMapping("/getAllData")
    public List<User> getAllData(){
        return demoMapper.getAllData();
    }


        @GetMapping("/touduyu")
    public ResultBean<String> redire(HttpServletRequest request, HttpServletResponse response){
        try{
                response.sendRedirect("https://touduyu.com/");
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResultBean<>(ResultBean.SUCCESS,"success");
    }
}





















