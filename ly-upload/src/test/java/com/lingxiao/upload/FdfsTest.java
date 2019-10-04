package com.lingxiao.upload;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FdfsTest {
    @Autowired
    private FastFileStorageClient storageClient;
    @Autowired
    private ThumbImageConfig imageConfig;

    @Test
    public void testUpload(){
        File file = new File("E:\\ftpfile\\img\\1.jpg");
        try {
            StorePath storePath = storageClient.uploadFile(new FileInputStream(file), file.length(), "jpg", null);
            System.out.println("带分组的路径"+storePath.getFullPath());
            System.out.println("不带分组的路径"+storePath.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUploadAndCrtThum(){
        File file = new File("E:\\ftpfile\\img\\1.jpg");
        try {
            StorePath storePath = storageClient.uploadImageAndCrtThumbImage(new FileInputStream(file), file.length(), "jpg", null);
            System.out.println("带分组的路径"+storePath.getFullPath());
            System.out.println("不带分组的路径"+storePath.getPath());


            String thumbImagePath = imageConfig.getThumbImagePath(storePath.getPath());
            System.out.println("缩略图  "+thumbImagePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
