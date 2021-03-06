package edu.scu.qz.service.impl;

import com.google.common.collect.Lists;
import edu.scu.qz.service.IFileService;
import edu.scu.qz.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        String uploadFilename = UUID.randomUUID().toString() + "." + fileExtensionName;
        logger.info("开始上传文件，上传文件的文件名：{}, 上传的路径：{}, 新文件名：{}", fileName, path, uploadFilename);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path, uploadFilename);

        try {
            file.transferTo(targetFile);
            // 将 targetFile 上传到 FTP 服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            // 上传完毕，删除 upload 文件夹下文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常", e);
        }
        return targetFile.getName();
    }

}
