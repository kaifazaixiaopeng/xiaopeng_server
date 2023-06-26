package com.xiaopeng.server.vx.utils;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

@Slf4j
@Component
public class RSAUtil {

    /**
     * 密钥长度 于原文长度对应 以及越长速度越慢
     */
    private final static int KEY_SIZE = 1024;
    public final static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDQYtxEEusY8t2TaApxjYlAlS777Rx3sqE5NFZm\n" +
            "4KSpEQpSzFKD3rcCbq5Nn6UL6kV9NpFS7qrPiY4kKY5g5AqamJB8Vgntk1C1EHrnLAWBhGp0E7aF\n" +
            "sCykpbnr+PEBMuKVCrw50qprn8LgEfzZzhUsZWDzJAwzt1sKdX48f9iiQQIDAQAB";
//    private final static String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANBi3EQS6xjy3ZNoCnGNiUCVLvvt\n" +
//            "HHeyoTk0VmbgpKkRClLMUoPetwJurk2fpQvqRX02kVLuqs+JjiQpjmDkCpqYkHxWCe2TULUQeucs\n" +
//            "BYGEanQTtoWwLKSluev48QEy4pUKvDnSqmufwuAR/NnOFSxlYPMkDDO3Wwp1fjx/2KJBAgMBAAEC\n" +
//            "gYA6xEZ95B/sz0DxexvG3jVgwzGW1k167CNPcJUVOjD1hNq2cuyqR7qwsunw9h6jd5PVNfhRvQDq\n" +
//            "uk4doKKIbIcYg6WAK/U2PcIPGGBLJZaN3yASA0qUdhWMoQwTX6417fFg+MNRrx0AU1qNqsnVVft6\n" +
//            "tYip5jcJXY7+j1YutoxnYQJBAO+hnaU6eOYgZCqviEDAVpN+dAOqIvZEJKoh27vxdNgf5l9tbpZT\n" +
//            "jMtvcikedDCysC6tY7YXrgWiwpR6ecUZMy0CQQDent4Bbv4RcL2Rq4T01OyWQIFU/fS80RvpogfR\n" +
//            "7u6dFxRdElAXbhC3h9s0pHD5tM7PExY35PWMl+UPIRjYyiflAkEAss2P+MrhjS0cMLGbZF1f/Fw9\n" +
//            "qsJXphQAm6X59IndWqqq1gesnaSoCOAWedIMdo3OYnXkUennYrMop2uf0s7T3QJAHP0m/5zKbM73\n" +
//            "gH6LQ65uWGpx7PVc5zF7pwK4bfflDoOw3SXDVCm694szWjmHlJycyY48uG6SIwqsgznWGHTKmQJA\n" +
//            "e4Pj+3Mqy/0vnsbj9kEihH8HbfnDf0MXmgETEKNmFxfJSd0do/D/q7j5bbHD8kYgEu76uGz8DLnG\n" +
//            "wcCHjRGxyA==";


    /**
     * 随机生成密钥对
     * @throws Exception
     */
    public static Map genKeyPair() throws Exception {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器
        keyPairGen.initialize(KEY_SIZE, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyString = encryptBASE64(publicKey.getEncoded());
        // 得到私钥字符串
        String privateKeyString = encryptBASE64(privateKey.getEncoded());
        // 将公钥和私钥保存到Map
        Map<Integer, String> keyMap = new HashMap<Integer, String>();
        //0表示公钥
        keyMap.put(0, publicKeyString);
        //1表示私钥
        keyMap.put(1, privateKeyString);
        return keyMap;
    }

    /**
     * RSA公钥加密
     *
     * @param str       加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public static String encrypt(String str, String publicKey) throws Exception {
        //base64编码的公钥
        byte[] decoded = decryptBASE64(publicKey);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = encryptBASE64(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param str        加密字符串
     * @param privateKey 私钥
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = decryptBASE64(str);
        //base64编码的私钥
        byte[] decoded = decryptBASE64(privateKey);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }

    /**
     * 验签
     *
     * @param params     请求参数
     * @return
     * @throws IOException
     */
    public static boolean validation(JSONObject params) throws IOException {
        // 拿出请求签名
        String sign = params.getString("sign");
        //签名不参与计算
        params.remove("sign");
        //重签
        String targetSign = getSign(params);

        // 校验签名
        if (!StringUtils.equals(targetSign, sign)) {// APPID查询的密钥进行签名 和 用户签名进行比对
            log.error("签名错误");
            return false;
        }
        // 校验签名是否失效
        long thisTime = System.currentTimeMillis() - params.getLong("date_time");
        //签名验证时间（TIMES =分钟 * 秒 * 毫秒） 当前设置为：5分钟有效期
        Integer TIMES = 5 * 60 * 1000;
        if (thisTime > TIMES) {// 比对时间是否失效
            log.error("签名失效");
            return false;
        }
        return true;
    }

    /**
     * 计算签名
     * @param params
     * @return
     */
    public static String getSign(JSONObject params){
        // 参数进行字典排序，生成待签名字符串
        String sortStr = getFormatParams(params);
        // 使用md5算法加密待加密字符串并转为大写即为sign
        String sign = SecureUtil.md5(sortStr).toUpperCase();
        return sign;
    }


    //编码返回字符串
    public static String encryptBASE64(byte[] key) throws Exception {
        return (new BASE64Encoder()).encodeBuffer(key);
    }

    //解码返回byte
    public static byte[] decryptBASE64(String key) throws Exception {
        return (new BASE64Decoder()).decodeBuffer(key);
    }

    /**
     * 参数字典排序
     *
     * @param params
     * @return
     */
    public static String getFormatParams(Map<String, Object> params) {
        List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(params.entrySet());
        Collections.sort(infoIds, new Comparator<Map.Entry<String, Object>>() {
            public int compare(Map.Entry<String, Object> arg0, Map.Entry<String, Object> arg1) {
                return (arg0.getKey()).compareTo(arg1.getKey());
            }
        });
        String ret = "";
        for (Map.Entry<String, Object> entry : infoIds) {
            ret += entry.getKey();
            ret += "=";
            ret += entry.getValue();
            ret += "&";
        }
        return ret;
    }

    /**
     * 获取客户端GET请求中所有的请求参数
     *
     * @param request
     * @return
     */
    public static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
        Map<String, String> res = new HashMap<String, String>();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                res.put(en, value);
                //如果字段的值为空，判断若值为空，则删除这个字段>
                if (null == res.get(en) || "".equals(res.get(en))) {
                    res.remove(en);
                }
            }
        }
        return res;
    }




    public static void main(String[] args) {
        //解密数据
        try {
            //生成公钥和私钥
            Map<Integer, String> keyMap = (Map<Integer, String>)genKeyPair();
            String publicKey = keyMap.get(0);
            System.out.println("公钥:" + publicKey);
            String privateKey = keyMap.get(1);
            System.out.println("私钥:" + privateKey);

            Map params = new HashMap();
            params.put("cid", "kmdl11061");
            params.put("cdate", "1571281896");
            //参数进行字典排序, 待签名字符串
            String sortStr = getFormatParams(params);
            // 使用md5算法加密待加密字符串并转为大写即为sign
            String sign = SecureUtil.md5(sortStr).toUpperCase();

            String orgData = "123456781668745014120123D";
            System.out.println("原数据：" + orgData);
            String encryptStr =encrypt(orgData,publicKey);
            System.out.println("加密结果：" + encryptStr);

            String decryptStr = decrypt(encryptStr,privateKey);
            System.out.println("解密结果：" + decryptStr);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
