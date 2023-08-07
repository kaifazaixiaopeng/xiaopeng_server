package com.xiaopeng.server.vx.controller;

import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.server.vx.config.AutoLog;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.RSAUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: RestController
 * @Author: BUG-WZP
 * @Since: 2023/6/9
 * @Remark:
 */
@RestController
@RequestMapping("/wechart")
@Slf4j
@Api(tags = "wechart")
public class MyController {


    @PostMapping("/delete")
    public void delete(@RequestParam("ids") List<Integer> ids) {
        System.out.println(JSONObject.toJSONString(ids));
    }

    /**
     * 解密:
     * 继承HttpServletResponseWrapper 类去重写getParameterValues，getParameter等方法
     * 加密:
     * 继承HttpServletRequestWrapper类，supports()方法返回值决定了beforeBodyRead()方法是否需要执行
     *
     * @param
     */
    @GetMapping("/demoAOPRSA")
    @AutoLog
    public void demoAOPRSA(@RequestParam("userName") String userName) {
        System.out.println("进来了");
        System.out.println("解密后的参数===》" + JSONObject.toJSONString(userName));
    }

//    @GetMapping("/testRSA")
//    public void testRSA(){
//        System.out.println("testRSA");
//        //加密参数
//        reqByGet("http://localhost:8080/api/demo/demoAOPRSA?userName=admin");
//    }
//    public JSONObject reqByGet(String url) {
//        JSONObject jsonObject = new JSONObject();
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        CloseableHttpResponse response = null;
//        //url截取，获取所有参数
//        String[] parts = url.split("\\?");
//        url=parts[0];
//        String params = parts[1];
//        //进行公钥加密
//        //获取公钥私钥
//        Map<Integer, String> keyMap = null;
//        String publicKey;
//        String privateKey;
//        try {
//            keyMap = (Map<Integer, String>) RSAUtil.genKeyPair();
//            publicKey = keyMap.get(0);
//            System.out.println("公钥:" + publicKey);
//            privateKey = keyMap.get(1);
//            System.out.println("私钥:" + privateKey);
//            String encrypt = RSAUtil.encrypt(params, publicKey);
//            url=url+"?cip="+encrypt;
//            String resultString = "";
//            log.info("新url====》{}",url);
//            try {
//                HttpGet httpGet = new HttpGet(url);
//                response = httpclient.execute(httpGet);
//                if (response.getEntity() != null) {
//                    resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
////                resultMap.put("responseEntity", resultString);
////                resultMap.put("responseEntityGB2312", EntityUtils.toString(response.getEntity(), "GB2312"));
//                }
//                jsonObject.put("CODE", 200);
//                jsonObject.put("MSG", resultString);
//            } catch (Exception e) {
//                log.error("===》HttpUtil.get方法执行出错，url：{}，信息：{}", url, e.toString());
//                jsonObject.put("CODE", 500);
//                jsonObject.put("MSG", e.toString());
//            } finally {
//                try {
//                    if (response != null) {
//                        response.close();
//                    }
//                    httpclient.close();
//                } catch (IOException e) {
//                    log.error("===》HttpUtil.get方法关闭出错，url：{}，信息：{}", url, e.toString());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return jsonObject;
//    }

    //回复微信消息接口
    @PostMapping("/reply")
    public void reply(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 调用消息业务类接收消息、处理消息
        String respMessage = "";
//        String respMessage = replyMessageService.reply(request);

        // 响应消息
        PrintWriter out = response.getWriter();
        out.print(respMessage);
        out.close();
    }

    @ApiOperation("读写robots文件")
    @PostMapping("/updateRobots")
    public void robotsEdit(@RequestBody JSONObject jsonObject) throws IOException {
        //将字符串写进robots.txt文件
        String config = jsonObject.getString("config");
        FileInputStream fileInputStream = new FileInputStream("src/main/resources/static/robots.txt");
        FileWriter fileWriter = new FileWriter("src/main/resources/static/robots.txt");
        // 往文件重写内容
        fileWriter.write("");// 清空
        fileWriter.write("11111111111111");
        fileWriter.flush();
        fileWriter.close();
    }

}
