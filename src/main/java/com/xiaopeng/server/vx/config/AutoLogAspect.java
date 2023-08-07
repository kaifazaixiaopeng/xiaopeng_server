package com.xiaopeng.server.vx.config;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @ClassName: AutoLogAspect
 * @Author: Bugpeng
 * @Since: 2023/4/10
 * @Remark:
 */
@Aspect
@Component
@Slf4j
public class AutoLogAspect {

    @Autowired
    @Lazy
    public AutoLogAspect() {
    }

    @Pointcut("@annotation(AutoLog))")
    public void logPointCut() {
        log.info("aop进来了");
    }

    @Around(value = "logPointCut()")
    public Object auth(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        AutoLog annotation = method.getAnnotation(AutoLog.class);

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request = requestAttributes.getRequest();
        String baiinfoPK = request.getHeader("baiinfoPK");

//        //对请求参数进行解密
//        Map<Integer, String> keyMap = (Map<Integer, String>)RSAUtil.genKeyPair();
//        String publicKey = keyMap.get(0);
//        System.out.println("公钥:" + publicKey);
//        String privateKey = keyMap.get(1);
//        System.out.println("私钥:" + privateKey);

        Object[] args = pjp.getArgs();
        log.info("解密前===》"+JSONObject.toJSONString(args));
//        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(args[0]));
//        String cip = jsonObject.getString("cip");
//        String decryptStr =RSAUtil.decrypt(cip,baiinfoPK);
//        log.info("解密后===》"+JSONObject.toJSONString(decryptStr));
//        //继续执行原方法
//        //对返回值进行加密
//        log.info("加密前===>{}",JSONObject.toJSONString(proceed));
//        String encryptStr =RSAUtil.encrypt(JSONObject.toJSONString(args),publicKey);
//        log.info("加密后===>{}",encryptStr);
        return pjp.proceed(args);
    }
}

