package com.xiaopeng.server.app.bean.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * @Auto:BUGPeng
 * @Date:2022/5/26 14::23
 * @ClassName:JDBCRunSqlTest
 * @Remark: 国际化信息处理类
 */
@Component
@Slf4j
public class I18nUtil {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private LocaleResolver localeResolver;

    private HttpServletRequest getRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        } catch (Exception e) {
            log.info("没有获取到HttpServletRequest");
            return null;
        }
    }

    /**
     * 获取国际化信息
     *
     * @param key 对应键
     * @return 国际化信息
     */
    public String getMessage(String key) {
        return getMessage(key, null);
    }

    /**
     * 获取国际化信息
     *
     * @param key  对应键
     * @param args 对应参数
     * @return 国际化信息
     */
    public String getMessage(String key, Object[] args) {
        Locale locale = getLocale();
        if (locale != null) {
            return messageSource.getMessage(key, args, locale);
        } else {
//            return messageSource.getMessage(key, args, localeResolver.resolveLocale(getRequest()));
            return messageSource.getMessage(key, args, getLocale());
        }
    }

    /**
     * Locale
     *
     * @return
     */
    public Locale getLocale() {
        Locale locale = Locale.SIMPLIFIED_CHINESE;
        HttpServletRequest httpServletRequest = getRequest();
        if (httpServletRequest == null) {
            return locale;
        } else {
            Cookie[] cookies = httpServletRequest.getCookies();
            String lang = "";
            if (null != cookies) {
                for (Cookie ck : cookies) {
                    if ("LOCALE".equals(ck.getName())) {
                        lang = ck.getValue();
                        switch (lang) {
                            case "en":
                                locale = Locale.US;
                                break;
                            default:
                                locale = Locale.SIMPLIFIED_CHINESE;
                        }
                        break;
                    }
                }
            }
            return locale;
        }
    }

    private final static String CN_JAVA_DATAFORMAT = "yyyy-MM-dd HH:mm:ss";
    private final static String EN_JAVA_DATAFORMAT = "dd/MM/yyyy HH:mm:ss";

    /**
     * 根据中英文获取java日期格式
     *
     * @return
     */
    public String getJavaDataFormat() {
        return getDataFormatString(CN_JAVA_DATAFORMAT, EN_JAVA_DATAFORMAT);
    }


    private final static String CN_SQL_DATAFORMAT = "%Y-%m-%d %H:%i:%S";
    private final static String EN_SQL_DATAFORMAT = "%d/%m/%Y %H:%i:%S";

    /**
     * 根据中英文获取sql日期格式
     *
     * @return
     */
    public String getSqlDataFormat() {
        return getDataFormatString(CN_SQL_DATAFORMAT, EN_SQL_DATAFORMAT);
    }

    private String getDataFormatString(String cnSqlDataformat, String enSqlDataformat) {
        Locale locale = getLocale();
        if (locale == null) {
            return cnSqlDataformat;
        } else if (Locale.SIMPLIFIED_CHINESE.equals(locale)) {
            return cnSqlDataformat;
        } else if (Locale.US.equals(locale)) {
            return enSqlDataformat;
        } else {
            return enSqlDataformat;
        }
    }

}
