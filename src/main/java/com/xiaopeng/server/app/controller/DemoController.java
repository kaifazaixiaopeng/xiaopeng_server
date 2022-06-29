package com.xiaopeng.server.app.controller;

import com.xiaopeng.server.app.bean.common.ResultBean;
import com.xiaopeng.server.app.bean.utils.FtpUtils;
import com.xiaopeng.server.app.bean.utils.I18nUtil;
import com.xiaopeng.server.app.mapper.DemoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auto:BUGPeng
 * @Date:2022/4/2816:48
 * @ClassName:Demo
 * @Remark:
 */

@Slf4j
@RestController
@RequestMapping("/v1/demo")
public class DemoController {
    @Autowired
    private DemoMapper demoMapper;
    @Autowired
    private I18nUtil i18nUtil;

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
    public List<Map<String, Object>> getAllData() {
        return demoMapper.getAllData();
    }


    @GetMapping("/touduyu")
    public ResultBean<String> redire(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect("https://touduyu.com/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResultBean<>(ResultBean.SUCCESS, "success");
    }

    /**
     * ssh连接服务器
     *
     * @param response
     * @param request
     * @return
     */
    @GetMapping("/connectionBySsh")
    public List<String> connectionBySsh(HttpServletResponse response, HttpServletRequest request) {
        FtpUtils ftpUtils = new FtpUtils();
//        ChannelSftp channel = ftpUtils.connect("124.221.225.23", 22, "root", "wzp@java666");
//        ftpUtils.disConnect(channel);
        List<String> strings = null;
        try {
            strings = ftpUtils.listFiles("/", "124.221.225.23", 22, "root", "wzp@java666");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strings;
    }

    /**
     * 通过获取Locale处理中英文转换
     */
    @GetMapping("/I18n")
    public List<String> getI18() {
        List<String> booleans = new ArrayList<>();
        /**
         * Locale包含多地区枚举，i18nUtil.getLocale()获取本地地区转换为枚举值比较
         */
        booleans.add(i18nUtil.getLocale() == Locale.SIMPLIFIED_CHINESE ? "成功" : "SUCCESS");
        booleans.add(i18nUtil.getLocale() == Locale.US ? "SUCCESS" : "成功");
        return booleans;
    }

    @GetMapping("/timeDemo")
    public Map<String, String> getTime() {
        Calendar cale = Calendar.getInstance();
        ;
        cale.add(Calendar.MONTH, -1);
        String startTime = new SimpleDateFormat("yyyy-MM-dd").format(cale.getTime());
        String endTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Map<String, String> map = new HashMap<>();
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        return map;
    }


//    @GetMapping("/pageList")
//    public List getFlowConfigs(@RequestParam("page") int page, @RequestParam("size") int size) {
//        return demoMapper.getAllData();
//    }

}





















