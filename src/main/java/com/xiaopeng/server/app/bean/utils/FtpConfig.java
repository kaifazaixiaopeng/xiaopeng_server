package com.xiaopeng.server.app.bean.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ftp")
public class FtpConfig {

    private String ftpHost;
    private int ftpPort;
    private String ftpUserName;
    private String ftpPassword;
    private String remoteDir;
    private String localDir;

}
