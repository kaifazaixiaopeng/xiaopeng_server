package com.xiaopeng.server.xiaopeng_server;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.InetAddress;

@Slf4j
@SpringBootApplication
@EnableScheduling
@MapperScan(basePackages = {"com.xiaopeng.server.xiaopeng_server.mapper"})
@EnableTransactionManagement
public class XiaopengServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(XiaopengServerApplication.class, args);
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:{}\n\t" +
                "External: \thttp://{}:{}\n\t" +
                "Doc: \thttp://{}:{}{}/doc.html\n" +
                "\t---xiaopeng_server started successfully\n" +
                "\t---小鹏的服务启动成功---\n" +
                "----------------------------------------------------------");
    }
}


