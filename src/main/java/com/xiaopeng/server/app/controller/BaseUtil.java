package com.xiaopeng.server.app.controller;

import org.omg.IOP.Encoding;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author yushuo
 * @Date: 2022/06/16/17:45
 */
public class BaseUtil {



//    /**
//     * 给字符串加密
//     */
//    public static String encode (String text){
//        return encode.encodeToString(text.getBytes(StandardCharsets.UTF_8));
//    }
//
//
//    /**
//     * 给加密后的字符串进行解密
//     */
//    public static String decoder (String encodeText){
//        return new String(decoder.decode(encodeText),StandardCharsets.UTF_8);
//    }


    public static void main(String[] args) {
        String testStr = "事项主要内容123415~!@#$&*()_+-=*,./?";
        String testStr1 = "zzzzsssssdddffqqqe11";
        String encodes = Base64.getEncoder().encodeToString(testStr.getBytes());
        String s = new String(Base64.getDecoder().decode(encodes), StandardCharsets.UTF_8);
        System.out.println(encodes);
        System.out.println(s);

    }



}
