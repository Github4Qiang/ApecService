package edu.scu.qz.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FTPUtil {

    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getProperty("ftp.ip");
    private static String ftpUsername = PropertiesUtil.getProperty("ftp.username");
    private static String ftpPassword = PropertiesUtil.getProperty("ftp.password");

    private String ip;
    private int port;
    private String username;
    private String password;
    private FTPClient ftpClient;

    public FTPUtil(String ip, int port, String username, String password) {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public static boolean uploadFile(String remotePath, List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp, 21, ftpUsername, ftpPassword);
        logger.info("开始连接FTP服务器");
        boolean result = ftpUtil.uploadFile2FTP(remotePath, fileList);
        logger.info("结束上传，上传结果：{}", result);
        return result;
    }

    // 默认上传至图片文件夹中
    public static boolean uploadFile(List<File> fileList) throws IOException {
        return uploadFile("img", fileList);
    }

    private boolean uploadFile2FTP(String remotePath, List<File> fileList) throws IOException {
        boolean uploaded = false;
        FileInputStream fis = null;
        if (connectFtpServer(ip, port, username, password)) {
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for (File fileItem : fileList) {
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(), fis);
                }
                uploaded = true;
            } catch (IOException e) {
                logger.error("上传文件异常", e);
            } finally {
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }

    private boolean connectFtpServer(String ip, int port, String username, String password) {
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(username, password);
        } catch (IOException e) {
            logger.error("连接FTP服务器异常", e);
        }
        return isSuccess;
    }

}
