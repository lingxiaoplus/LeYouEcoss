package com.lingxiao.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.lingxiao.enums.ExceptionEnum;
import com.lingxiao.exception.LyException;
import com.lingxiao.upload.config.UploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service("uploadService")
@Slf4j
@EnableConfigurationProperties(UploadProperties.class)
public class UploadService {

    @Autowired
    private FastFileStorageClient storageClient;
    @Autowired
    private UploadProperties uploadProperties;
    public String uploadImage(MultipartFile file) {
        //校验文件类型
        String contentType = file.getContentType();
        if (!uploadProperties.getAllowSuffix().contains(contentType)){
            throw new LyException(ExceptionEnum.FILE_TYPE_NOT_SUPPORT);
        }
        try {
            BufferedImage read = ImageIO.read(file.getInputStream());
            if (read == null){
                throw new LyException(ExceptionEnum.FILE_TYPE_NOT_SUPPORT);
            }
            //传统文件上传方式
            /*String dir = this.getClass().getClassLoader().getResource("").getPath();
            System.out.println("路径"+dir);
            File dirFile = new File(dir);
            if (!dirFile.exists()){
                dirFile.setWritable(true);
                dirFile.mkdirs();
            }
            File dest  = new File(dir + "/" + file.getOriginalFilename());
            file.transferTo(dest);*/

            //获取文件名后缀
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");

            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);

            return uploadProperties.getBaseUrl() + storePath.getFullPath();
        } catch (IOException e) {
            log.error("上传图片失败",e);
            throw new LyException(ExceptionEnum.FILE_TYPE_NOT_SUPPORT);
        }
    }
}
