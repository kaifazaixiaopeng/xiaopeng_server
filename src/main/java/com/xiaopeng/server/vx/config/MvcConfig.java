package com.xiaopeng.server.vx.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName: MvcConfig
 * @Author: BUG-WZP
 * @Since: 2023/8/7
 * @Remark:
 */
@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {


    /**
     * 视图跳转控制器
     * 无业务逻辑的跳转 均可以以这种方法写在这里
     *
     * @param registry
     */
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("wes");
//        registry.addViewController("/home").setViewName("wes");
//        registry.addViewController("/login").setViewName("login");
//    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //告知系统static 当成 静态资源访问
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }

}
