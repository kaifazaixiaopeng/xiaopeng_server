package com.xiaopeng.server;

import com.xiaopeng.server.app.bean.MyBatisRepository;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@SpringBootApplication
@EnableScheduling
@MapperScan(basePackages = {"com.xiaopeng.server.app.dao"},annotationClass = MyBatisRepository.class)
@EnableTransactionManagement
public class XiaopengServerApplication {

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext run = SpringApplication.run(XiaopengServerApplication.class, args);
        SpringApplication springApplication = new SpringApplication(XiaopengServerApplication.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        Environment env = run.getEnvironment();
        log.info("\n=======================================================\n\t" +
                "Local: \t\thttp://localhost:{}\n\t" +
                "External: \thttp://{}:{}\n\t" +
                "Doc: \thttp://{}:{}{}/doc.html\n" +
                "\t---xiaopeng_server started successfully\n" +
                "\t---小鹏的服务启动成功---\n" +
                "==========================================================",
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                env.getProperty("server.servlet.context-path"));
    }
}


