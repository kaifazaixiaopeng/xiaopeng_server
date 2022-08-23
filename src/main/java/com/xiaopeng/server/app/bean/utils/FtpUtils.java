package com.xiaopeng.server.app.bean.utils;


import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

@Slf4j
public class FtpUtils {
    //指定重试的次数
    private long count;
    //重试次数
    private long count1 = 0;
    //重试间隔时间
    private long sleepTime=6000;

    /**
     * 连接sftp远程服务器
     * @param ftpHost    ip
     * @param ftpPort    端口
     * @param ftpUserName  用户名
     * @param ftpPassword   密码
     * @return
     */
    public ChannelSftp connect(String ftpHost, int ftpPort, String ftpUserName, String ftpPassword) {
        ChannelSftp sftp = null;
        try {
            JSch jsch = new JSch();
            jsch.getSession(ftpUserName, ftpHost, ftpPort);
            Session sshSession = jsch.getSession(ftpUserName, ftpHost, ftpPort);
            log.info("Session created ... UserName=" + ftpUserName + ";host=" + ftpHost + ";port=" + ftpPort);
            log.info("session timeout =:{} ", sshSession.getTimeout());
            sshSession.setPassword(ftpPassword);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            log.info("Session connected ...");
            log.info("Opening Channel ...");
            Channel channel = sshSession.openChannel("sftp");
            //设置连接超时时间，有的服务器登陆比较慢，这里需重新设置时间长一点
            channel.connect(120000);
            sftp = (ChannelSftp) channel;
            log.info("登录成功");
        } catch (Exception e) {
            try {
                count1 += 1;
                if (count == count1) {
                    throw new RuntimeException(e);
                }
                Thread.sleep(sleepTime);
                log.info("retry Session connected....");
                connect(ftpHost,ftpPort,ftpUserName,ftpPassword);
            } catch (InterruptedException e1) {
                throw new RuntimeException(e1);
            }
        }
        return sftp;
    }



    /**
     * 断掉连接
     */
    public void disConnect(ChannelSftp sftp) {
        try {
            sftp.disconnect();
            sftp.getSession().disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkFileName(String fileName) {
        if (".".equals(fileName) || "..".equals(fileName)) {
            return true;
        }
        return false;
    }

    private String getRemoteFilePath(String remoteFilePath, String fileName) {
        if (remoteFilePath.endsWith("/")) {
            return remoteFilePath.concat(fileName);
        } else {
            return remoteFilePath.concat("/").concat(fileName);
        }
    }

    public FtpUtils(long count, long sleepTime) {
        this.count = count;
        this.sleepTime = sleepTime;
    }

    public FtpUtils() {

    }
    /**
     * 进入指定文件夹拿到所有文件名称
     * @param directory
     * @param ftpHost
     * @param ftpPort
     * @param ftpUserName
     * @param ftpPassword
     * @return
     * @throws SftpException
     */
    public List<String> listFiles(String directory,String ftpHost, int ftpPort, String ftpUserName, String ftpPassword) throws SftpException {
        ChannelSftp sftp = connect(ftpHost,ftpPort,ftpUserName,ftpPassword);
        List fileNameList = new ArrayList();
        try {
            sftp.cd(directory);
        } catch (SftpException e) {
            return fileNameList;
        }
        Vector vector = sftp.ls(directory);
        for (int i = 0; i < vector.size(); i++) {
            if (vector.get(i) instanceof ChannelSftp.LsEntry) {
                ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) vector.get(i);
                String fileName = lsEntry.getFilename();
                if (".".equals(fileName) || "..".equals(fileName)) {
                    continue;
                }
                fileNameList.add(fileName);
            }
        }
        disConnect(sftp);
        return fileNameList;
    }

//    /**
//     * 下载远程文件夹下的所有文件
//     *
//     * @param urlStrings
//     * @param dirStrings
//     * @throws Exception
//     */
//    public void getRoleIncreaseFileDir(List<String> urlStrings, List<String> dirStrings, FtpConfig sftpConfig) throws Exception {
//        ChannelSftp channelSftp = connect(sftpConfig);
//        for (int i = 0; i < urlStrings.size(); i++) {
//            String remoteFilePath = urlStrings.get(i);
//            String localDirPath = dirStrings.get(i);
//            File localDirFile = new File(localDirPath);
//            // 判断本地目录是否存在，不存在需要新建各级目录
//            if (!localDirFile.exists()) {
//                localDirFile.mkdirs();
//            }
//            if (log.isInfoEnabled()) {
//                log.info("sftp文件服务器文件夹[{}],下载到本地目录[{}]", new Object[]{remoteFilePath, localDirFile});
//            }
//            Vector<ChannelSftp.LsEntry> lsEntries = channelSftp.ls(remoteFilePath);
//            if (log.isInfoEnabled()) {
//                log.info("远程目录下的文件为[{}]", lsEntries);
//            }
//            for (ChannelSftp.LsEntry entry : lsEntries) {
//                String fileName = entry.getFilename();
//
//                if (checkFileName(fileName)) {
//                    continue;
//                }
//                String remoteFileName = getRemoteFilePath(remoteFilePath, fileName);
//                if (fileName.endsWith(".txt")) {
//                    Files file = new Files();
//                    file.setFile_name(fileName);
//                    file.setFile_type("txt");
//                    file.setFile_path(remoteFileName);
//                    file.setFile_size(entry.getAttrs().getSize());
//                    file.setFile_date(DateUtil.parse(entry.getAttrs().getMtimeString()));
//                    file.setState(0);
//
//                    Files isExist = filesMapper.selectOne(new LambdaQueryWrapper<Files>()
//                            .eq(Files::getFile_path, remoteFileName));
//                    if (isExist == null) {
//                        channelSftp.get(remoteFileName, localDirPath);
//                        filesMapper.insert(file);
//                    }
//                }
//            }
//        }
//        disConnect(channelSftp);
//    }

//    /**
//     * 下载远程文件夹下的所有文件
//     *
//     * @param remoteFilePath
//     * @param localDirPath
//     * @throws Exception
//     */
//    public void getFileDir(String remoteFilePath, String localDirPath, FtpConfig sftpConfig) throws Exception {
//
//        File localDirFile = new File(localDirPath);
//        // 判断本地目录是否存在，不存在需要新建各级目录
//        if (!localDirFile.exists()) {
//            localDirFile.mkdirs();
//        }
//        if (log.isInfoEnabled()) {
//            log.info("sftp文件服务器文件夹[{}],下载到本地目录[{}]", new Object[]{remoteFilePath, localDirFile});
//        }
//        ChannelSftp channelSftp = connect(sftpConfig);
//        Vector<ChannelSftp.LsEntry> lsEntries = channelSftp.ls(remoteFilePath);
//        if (log.isInfoEnabled()) {
//            log.info("远程目录下的文件为[{}]", lsEntries);
//        }
//        for (ChannelSftp.LsEntry entry : lsEntries) {
//            String fileName = entry.getFilename();
//
//            if (checkFileName(fileName)) {
//                continue;
//            }
//            String remoteFileName = getRemoteFilePath(remoteFilePath, fileName);
//            if (fileName.endsWith(".txt")) {
//                Files file = new Files();
//                file.setFile_name(fileName);
//                file.setFile_type("txt");
//                file.setFile_path(remoteFileName);
//                file.setFile_size(entry.getAttrs().getSize());
//                file.setFile_date(DateUtil.parse(entry.getAttrs().getMtimeString()));
//                file.setState(0);
//
//                Files isExist = filesMapper.selectOne(new LambdaQueryWrapper<Files>()
//                        .eq(Files::getFile_path, remoteFileName));
//                if (isExist == null) {
//                    channelSftp.get(remoteFileName, localDirPath);
//                    filesMapper.insert(file);
//                }
//            }
//        }
//        disConnect(channelSftp);
//    }
}

