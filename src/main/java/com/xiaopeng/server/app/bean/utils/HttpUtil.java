package com.xiaopeng.server.app.bean.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auto:BUGPeng
 * @Date:2022/5/26 14::23
 * @ClassName:JDBCRunSqlTest
 * @Remark:  http请求工具类
 */
@Slf4j
@Component
public class HttpUtil {

    private static final String CODE = "CODE";
    private static final String MSG = "MSG";
    private final Map<String, String> resultMap = new HashMap<>();
    /**
     * 通用，http get请求
     * @param url 请求地址
     * @return
     */
    public Map<String, String> sendByGet(String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            log.info("========= 请求地址：{} ==========", url);
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Content-Type", "application/json");
            response = httpclient.execute(httpGet);
            if (response.getEntity() != null) {
                log.info("==== 返回非空 ===");
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
//                resultMap.put("responseEntity", resultString);
//                resultMap.put("responseEntityGB2312", EntityUtils.toString(response.getEntity(), "GB2312"));
            }
            log.info("========= 响应报文：{} ==========", resultString);
            resultMap.put(CODE, "0");
            resultMap.put(MSG, resultString);
        } catch (Exception e) {
            log.error("===》HttpUtil.get方法执行出错，url：{}，信息：{}", url, e.toString());
            resultMap.put(CODE, "1");
            resultMap.put(MSG, e.toString());
            resultMap.put("responseEntity", "");
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
        return resultMap;
    }

    /**
     * 通用，http get请求
     * @param url
     * @return
     */
    public JSONObject reqByGet(String url) {
        JSONObject jsonObject = new JSONObject();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            log.info("========= 请求地址：{} ==========", url);
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Content-Type", "application/json");
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
            jsonObject.put("responseEntity", "");
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
        return jsonObject;
    }

    /**
     * 通用，http post请求
     * @param url
     * @param params
     * @return
     */
    public Map<String, String> sendByPost(String url, String params) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";

        try {
            log.info("========= 请求地址：{} ==========", url);
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json");
            if (params != null) {
                log.info("========= 请求参数：{} ==========", params);
                StringEntity entity = new StringEntity(params, ContentType.APPLICATION_JSON);
                httpPost.setEntity(entity);
            }
            response = httpclient.execute(httpPost);
            if (response.getEntity() != null) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
            log.info("========= 响应报文：{} ==========", resultString);
            resultMap.put(CODE, "0");
            resultMap.put(MSG, resultString);
        } catch (Exception e) {
            log.error("===》HttpUtil.post方法执行出错，url：{}，信息：{}", url, e.toString());
            resultMap.put(CODE, "1");
            resultMap.put(MSG, e.toString());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                log.error("===》HttpUtil.post方法关闭出错，url：{}，信息：{}", url, e.toString());
            }
        }
        return resultMap;
    }

    /**
     * 通用，http post请求
     * @param url
     * @param params
     * @return
     */
    public JSONObject sendpost(String url, String params) {
        JSONObject jsonObject = new JSONObject();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";

        try {
            log.info("========= 请求地址：{} ==========", url);
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json");
            if (params != null) {
                log.info("========= 请求参数：{} ==========", params);
                StringEntity entity = new StringEntity(params, ContentType.APPLICATION_JSON);
                httpPost.setEntity(entity);
            }
            response = httpclient.execute(httpPost);
            if (response.getEntity() != null) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
            log.info("========= 响应报文：{} ==========", resultString);
            jsonObject.put("CODE", "0");
            jsonObject.put("MSG", resultString);
        } catch (Exception e) {
            log.error("===》HttpUtil.post方法执行出错，url：{}，信息：{}", url, e.toString());
            jsonObject.put("CODE", "1");
            jsonObject.put("MSG", e.toString());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                log.error("===》HttpUtil.post方法关闭出错，url：{}，信息：{}", url, e.toString());
            }
        }
        return jsonObject;
    }


    /**
     * 不校验ssl
     * @param url
     * @param params
     * @return
     */
    public Map<String, String> httpPostIgnoreVerifySSL(String url, String params) {
        String resultString = "";
        CloseableHttpResponse response = null;
        CloseableHttpClient client = null;
        try {
            log.info("========= 请求地址：{} ==========", url);
            // 设置协议http和https对应的处理socket链接工厂的对象
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", buildSSLSocketFactory())
                    .build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            HttpClients.custom().setConnectionManager(connManager);
            // 创建自定义的httpclient对象
            client = HttpClients.custom().setConnectionManager(connManager).build();

            // 创建post方式请求对象
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json");
            if (params != null) {
                log.info("========= 请求参数：{} ==========", params);
                StringEntity entity = new StringEntity(params, ContentType.APPLICATION_JSON);
                httpPost.setEntity(entity);
            }
            // 执行请求操作，并拿到结果
            response = client.execute(httpPost);
            // 获取结果实体
            if (response.getEntity() != null) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
            log.info("========= 响应报文：{} ==========", resultString);
            resultMap.put(CODE, "0");
            resultMap.put(MSG, resultString);
        } catch (Exception e) {
            log.error("===》HttpUtil.httpPostIgnoreVerifySSL方法执行出错，url：{}，信息：{}", url, e.toString());
            resultMap.put(CODE, "1");
            resultMap.put(MSG, e.toString());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (client != null) {
                    client.close();
                }
            } catch (IOException e) {
                log.error("===》HttpUtil.httpPostIgnoreVerifySSL方法关闭出错，url：{}，信息：{}", url, e.toString());
            }
        }
        return resultMap;
    }
    /**
     * http请求绕过SSL校验--------TlSv1版本
     * @return
     * @throws Exception
     */
    private SSLConnectionSocketFactory buildSSLSocketFactory() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        // 设置信任证书（绕过TrustStore验证）
        sslContext.init(null, new TrustManager[] {new AuthX509TrustManager()}, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        return new SSLConnectionSocketFactory(sslContext,
                new String[] { "TLSv1" }, null, new HostnameVerifier() {
            // hostname,默认返回true,不验证hostname
            @Override
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        });
    }

    private static class AuthX509TrustManager implements TrustManager, X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType)
                throws CertificateException {
            return;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
            return;
        }
    }
    /**
     * 不校验ssl---返回String
     * @param url
     * @param params
     * @return
     */
    public String httpPostIgnoreVerifySSLToReturnString(String url, String params) {
        log.info("httpPostIgnoreVerifySSL======url:{}",url);
        log.info("httpPostIgnoreVerifySSL======params:{}",params);
        String resultString = "";
        CloseableHttpResponse response = null;
        CloseableHttpClient client = null;
        try {
            log.info("========= 请求地址：{} ==========", url);
            // 设置协议http和https对应的处理socket链接工厂的对象
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", gansuTLS())
                    .build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            HttpClients.custom().setConnectionManager(connManager);
            // 创建自定义的httpclient对象
            client = HttpClients.custom().setConnectionManager(connManager).build();

            // 创建post方式请求对象
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json");
            if (params != null) {
                log.info("========= 请求参数：{} ==========", params);
                StringEntity entity = new StringEntity(params, ContentType.APPLICATION_JSON);
                httpPost.setEntity(entity);
            }
            // 执行请求操作，并拿到结果
            response = client.execute(httpPost);
            // 获取结果实体
            if (response.getEntity() != null) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
            log.info("========= 响应报文：{} ==========", resultString);
        } catch (Exception e) {
            log.error("===》HttpUtil.httpPostIgnoreVerifySSL方法执行出错，url：{}，信息：{}", url, e.toString());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (client != null) {
                    client.close();
                }
            } catch (IOException e) {
                log.error("===》HttpUtil.httpPostIgnoreVerifySSL方法关闭出错，url：{}，信息：{}", url, e.toString());
            }
        }
        return resultString;
    }
    /**
     * http请求绕过SSL校验---TLSv1.2版本
     * @return
     * @throws Exception
     */
    private SSLConnectionSocketFactory gansuTLS() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        // 设置信任证书（绕过TrustStore验证）
        sslContext.init(null, new TrustManager[] {new AuthX509TrustManager()}, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        return new SSLConnectionSocketFactory(sslContext,
                new String[] { "TLSv1.2" }, null, new HostnameVerifier() {
            // hostname,默认返回true,不验证hostname
            @Override
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        });
    }
}
