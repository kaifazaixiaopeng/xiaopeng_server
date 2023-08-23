package com.xiaopeng.server.app.controller;

import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.server.app.bean.common.ResultBean;
import com.xiaopeng.server.app.bean.utils.FtpUtils;
import com.xiaopeng.server.app.bean.utils.I18nUtil;
import com.xiaopeng.server.app.mapper.DemoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
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
        List<String> ids = new ArrayList<>();
        ids.add("1");
        ids.add("2");
        ids.add("3");
        model.addAttribute("hi", "小鹏主机的第一个程序!");
        model.addAttribute("testList",ids);
        return new ResultBean(ResultBean.SUCCESS, "Hello,小鹏!");
    }

    @GetMapping("/bio")
    public File getCSVFile() {
        return new File("test.txt");
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
        cale.add(Calendar.MONTH, -1);
        String startTime = new SimpleDateFormat("yyyy-MM-dd").format(cale.getTime());
        String endTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Map<String, String> map = new HashMap<>();
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        return map;
    }

    @PostMapping("/resumeFile")
    public void getResumeFile(HttpServletRequest request, HttpServletResponse response) {
        FileInputStream in = null;
        OutputStream out = null;
        try {
            response.setContentType("application/pdf");
            //获取文件
            String path = request.getSession().getServletContext().getRealPath("/tmp/简历.pdf");
            File file = new File(path);
            in = new FileInputStream(file);
            out = response.getOutputStream();
            byte[] b = new byte[1024];
            while ((in.read(b)) != -1) {
                out.write(b);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert in != null;
                in.close();
                assert out != null;
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //    @GetMapping("/pageList")
//    public List getFlowConfigs(@RequestParam("page") int page, @RequestParam("size") int size) {
//        return demoMapper.getAllData();
//    }
//
//    public static void main(String[] args) {
//        /**
//         * 格式化字符串，不足位数用0补齐
//         */
//        String noStr;
//        Integer snmSerialLen=10;
//        Integer snmSerialNo=10;
//        if (snmSerialLen != null) {
//            noStr = String.format("%0" + snmSerialLen + "d", snmSerialNo);
//        } else {
//            noStr = snmSerialNo + "";
//        }
//        System.out.println(noStr);
//        /**
//         * 使用糊涂的包，进行位数补齐格式化，
//         * padPre---在目标前补齐
//         * padAfter---在目标后补齐
//         * center---两边同时补齐
//         */
//        String s="token";
//        char c='#';
//        String center = StrUtil.padPre(s, 11, c);
//        System.out.println(center);
//        /**
//         * 根据类型截取，格式化时间
//         */
//        String snmTimemode = "1";
//        String snmResetTime = "2022-08-24 12:00:00";   // 格式 yyyy-MM-dd HH:mm:ss
//        String dateString = "";
//        if (StringUtils.isNotBlank(snmTimemode)) {
//            switch (snmTimemode) {
//                case "1":   // 位数精确到日
//                    dateString = snmResetTime.substring(0, 10);
//                    break;
//                case "2":   // 位数精确到月
//                    dateString = snmResetTime.substring(0, 7);
//                    break;
//                case "3":   // 位数精确到年
//                    dateString = snmResetTime.substring(0, 4);
//                    break;
//                case "4":   // 位数丢弃日期时间
//                    dateString = "";
//                    break;
//                default:
//                    break;
//            }
//            System.out.println(dateString = dateString.replace("-", ""));
//        }
//    }
//    public static void main(String[] args) {
//        String A = "a";
//        String B = "b";
//        A += "${B}";
//        System.out.println(A);
//    }

    private static void method(String num) {
        switch (num) {
            case "th":
                System.out.println("th");
            case "null":
                System.out.println("null");
            default:
                System.out.println("default");
        }
    }

    //    public static void main(String[] args) {
//        JSONObject jsonObject = new JSONObject();
//        try{
//            jsonObject.put("异常测试test","yushuo");
//            int i=1/0;
//        }catch (Exception e){
//            e.printStackTrace();
//            jsonObject.put("添加成功",e.getMessage());
//        }
//        System.out.println(JSONObject.toJSONString(jsonObject));
//    }
    /**
     * 驼峰转换
     */
    private static final char SEPARATOR = '_';
//    public static void main(String[] args) {
//        try {
//        /**
//         * 我写的
//         *  record：
//         *  string类有对应方法：.toLowerCase 将字符串转为小写
//         *                   .toUpperCase 将字符串转为大写
//         *   char也是对应方法
//         */
////            String word = "Hello_World";
////            String s = word.toLowerCase();
////            StringBuilder sb = new StringBuilder();
////            boolean flag = false;
////            for (int i = 0; i < s.length(); i++) {
////                char c = s.charAt(i);
////                if (c!=SEPARATOR) {
////                    if (flag ) {
////                        if (c >= 'a' && c <= 'z'){
////                            c=Character.toUpperCase(c);
////                    }
////                        sb.append(c);
////                        flag=false;
////                    } else {
////                        sb.append(c);
////                    }
////                } else if (c == SEPARATOR) {
////                    flag = true;
////                } else {
////                    sb.append(c);
////                    flag=false;
////                }
////            }
//        /**
//         * 项目大佬写的
//         */
//        String s="Hello_World";
////        if (s == null) {
////            return null;
////        }
//        //将传入的字符串直接全部转为小写
//        s = s.toLowerCase();
//        //建立同等长度的stringbuilder
//        StringBuilder sb = new StringBuilder(s.length());
//        //创建flag
//        boolean upperCase = false;
//        //遍历s
//        for (int i = 0; i < s.length(); i++) {
//            char c = s.charAt(i);
//            //判断当前字符是不是下划线
//            if (c == SEPARATOR) {
//                //修改flag标记，因为下划线后首字母为大写
//                upperCase = true;
//            } else if (upperCase) {
//                //标记为true时修改首字母为大写
//                sb.append(Character.toUpperCase(c));
//                upperCase = false;
//            } else {
//                sb.append(c);
//            }
//        }
//            System.out.println(sb.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    /**
     * 输入:
     * s = "acdcb"
     * p = "a*cb"
     * 输出: true
     */
    public static void main(String[] args) {
//        String s1="abcdcb";
//        String s2="a*cb";
//        methodToCompare(s1,s2);
        System.out.println(MyEnum.getFromCode("psn_1").getCode());

    }

    private static boolean methodToCompare(String s1, String s2) {
        //首先判断是不是包含特殊通配符
        char[] chars = s1.toCharArray();
        char[] chars1 = s2.toCharArray();
        boolean flag=false;
        if(!s2.contains("*")||!s1.contains("*")){
            //不包含特殊字符，直接比对长度，长度一致，比对是否相同
            if(s1.length()==s2.length()){
                for (int i = 0; i < chars.length; i++) {
                    char aChar = chars[i];
                    char bChar = chars1[i];
                    flag= String.valueOf(aChar).equals(String.valueOf(bChar));
                }
            }
        }else{
            /**
             包含特殊字符*号，那么怎么比对呢？碰到*号，ab*c  abcdc ，
             碰到星号，截取下，变成*c和cdc，然后从后方比对，带星对的串遍历到星结束
             *
             */
            //首先正向比对，直接死循环
            int tab=0;
            for(;;tab++){
                if(String.valueOf(chars[tab]).equals("*")||String.valueOf(chars1[tab]).equals("*")){
                    //碰到星号，记录tab的值，直接从此处开始反转两个数组
                    Character[] reChars=reversal(chars);
                    Character[] reChars1=reversal(chars1);

                }
            }
        }

        return flag;
    }

        private static Character[] reversal(char[] chars1) {

        char[] chars ={'1','2','3'};
        //数组转list
        List<String> list = new ArrayList<>();
        //反遍历chars加入到list
        for (int i = chars.length-1; i >=0; i--) {
            list.add(String.valueOf(chars[i]));
        }
//        list转数组
        Character[] strings = list.toArray(new Character[list.size()]);
        System.out.println(JSONObject.toJSONString(strings));
        return  strings;
    }

}





















