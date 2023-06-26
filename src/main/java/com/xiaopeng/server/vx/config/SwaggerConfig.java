//package com.xiaopeng.server.vx.config;
//
//import lombok.Data;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.Contact;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//import java.util.ArrayList;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @ClassName: SwaggerConfig
// * @Author: BUG-WZP
// * @Since: 2023/6/15
// * @Remark:
// */
//@Data
//@ConfigurationProperties(prefix = "pinda.swagger")
//public class SwaggerConfig {
//    /*
//     *配置属性类，用于封装接口文档相关属性，从配置文件读取信息封装成当前对象
//     */
//        private String title = "在线文档"; //标题
//        private String group = ""; //自定义组名
//        private String description = "在线文档"; //描述
//        private String version = "1.0"; //版本
//        private Contact contact = new Contact(); //联系人
//        private String basePackage = "com.itheima.pinda"; //swagger会解析的包路径
//        private List<String> basePath = new ArrayList<>(); //swagger会解析的url规则
//        private List<String> excludePath = new ArrayList<>();//在basePath基础上需要排除的url规则
//        private Map<String, DocketInfo> docket = new LinkedHashMap<>(); //分组文档
//        public String getGroup() {
//            if (group == null || "".equals(group)) {
//                return title;
//            }
//            return group;
//        }
//        @Data
//        public static class DocketInfo {
//            private String title = "在线文档"; //标题
//            private String group = ""; //自定义组名
//            private String description = "在线文档"; //描述
//            private String version = "1.0"; //版本
//            private Contact contact = new Contact(); //联系人
//            private String basePackage = ""; //swagger会解析的包路径
//            private List<String> basePath = new ArrayList<>(); //swagger会解析的url规则
//            private List<String> excludePath = new ArrayList<>();//在basePath基础上需要排除的url
//            public String getGroup() {
//                if (group == null || "".equals(group)) {
//                    return title;
//                }
//                return group;
//            }
//        }
//        @Data
//        public static class Contact {
//            private String name = "pinda"; //联系人
//            private String url = ""; //联系人url
//            private String email = ""; //联系人email
//        }
//    }
//
//
//}
