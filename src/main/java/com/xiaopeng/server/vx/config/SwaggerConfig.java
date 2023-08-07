package com.xiaopeng.server.vx.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @ClassName: SwaggerConfig
 * @Author: BUG-WZP
 * @Since: 2023/6/15
 * @Remark:
 */
@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {
    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                // 是否启用Swagger
                .enable(true)
                //分组名称
                .groupName("1.0版本")
                // 用来创建该API的基本信息，展示在文档的页面中（自定义展示的信息）
                .apiInfo(apiInfo())
                // 设置哪些接口暴露给Swagger展示
                .select()
                // 扫描所有有注解的api，用这种方式更灵活
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                //指定Controller扫描包路径
//                .apis(RequestHandlerSelectors.basePackage("com.example.controller"))
                // 扫描所有
//                .apis(RequestHandlerSelectors.any())
                .build();
        return docket;
    }

    /**
     * swagger声明项目信息
     *
     * @return
     */
    private ApiInfo apiInfo() {
        String name = "xiaopeng";
        Contact contact = new Contact(name, "", "");
        return new ApiInfoBuilder()
                .title("xiaopeng-server")
                .description("xiaopeng-server接口文档描述")
                .version("1.0")//版本
                .contact(contact)
                .build();
    }
}
