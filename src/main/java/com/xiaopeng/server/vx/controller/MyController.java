package com.xiaopeng.server.vx.controller;

import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.server.vx.config.AutoLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import okhttp3.RequestBody;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

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
    public void robotsEdit(@org.springframework.web.bind.annotation.RequestBody JSONObject jsonObject) throws IOException {
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

//    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public void uploadFiles(@RequestPart("files") MultipartFile[] files) {
//        // 处理文件上传逻辑 for (MultipartFile file : files) {
//        // 可以在这里执行保存文件的操作 }
//        // return ResponseEntity.ok("文件上传成功"); }
//
//    }

    /**
     * OKHttp3
     */
    //文件类型
    private static final okhttp3.MediaType MEDIA_TYPE_PNG = okhttp3.MediaType.parse("multipart/form-data");
    @ApiOperation("sendMultipart")
    @PostMapping("/sendMultipart")
    private void sendMultipart(HttpServletRequest httpServletRequest) {
        try {
            OkHttpClient client;
            client = new OkHttpClient();
            String jsonStr = "9998889988";
            Collection<Part> files = httpServletRequest.getParts();
            MultipartBody.Builder build = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            build.addFormDataPart("jsonStr", jsonStr);
            ForkJoinPool forkJoinPool = new ForkJoinPool(Math.min(files.size(), 10));
            forkJoinPool.submit(() -> files.parallelStream().forEach(multipartFile -> {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                InputStream inputStream = null;
                try {
                    //文件
                    inputStream = multipartFile.getInputStream();
                    byte[] b = new byte[2048];
                    int n;
                    while ((n = inputStream.read(b)) != -1) {
                        outputStream.write(b, 0, n);
                    }
                    build.addFormDataPart("files",
                            multipartFile.getName(),//具体file名字,可以截取
                            RequestBody.create(outputStream.toString(), MEDIA_TYPE_PNG));//第一个参数可以是file可以是byte[]可以是String   这里第二个参数是文件类型，是上面的常量，如果你是text类型，那么需要更改
//                build.addFormDataPart("files",outputStream.toString());
                } catch (Exception e) {
                    try {
                        outputStream.close();
                        assert inputStream != null;
                        inputStream.close();
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }finally {
                    try {
                        outputStream.close();
                        assert inputStream != null;
                        inputStream.close();
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }
            })).join();
            forkJoinPool.shutdown();
            RequestBody requestBody = build.build();
            Request request = new Request.Builder()
                    .url("http://192.168.0.184:8080/api/wechart/getFiles")//url请求地址，自己替换
//                    .addHeader("Content-Type","multipart/form-data")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e){}
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    log.info("异常回调响应:{}", Objects.requireNonNull(response.body()).string());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收
     *
     * @return
     */
    @PostMapping(value = "/getFiles")
    public void uploadFiles(HttpServletRequest request) {
        Collection<Part> files = null;
        try {
            files = request.getParts();
            for (Part file : files) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                InputStream inputStream = file.getInputStream();
                byte[] b = new byte[2048];
                int n;
                while ((n = inputStream.read(b)) != -1) {
                    outputStream.write(b, 0, n);
                }
                log.info("info:{}", outputStream.toString());
            }
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
        String jsonStr = request.getParameter("jsonStr");
        log.info("files:{}", JSONObject.toJSONString(files));
        log.info("jsonStr:{}", jsonStr);
    }



}
