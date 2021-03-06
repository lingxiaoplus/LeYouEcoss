package com.lingxiao.upload.web;

import com.lingxiao.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController("uploadController")
@RequestMapping("/upload")
public class UploadController {
    @Autowired
    private UploadService uploadService;

    /**
     * 上传文件
     * @param file
     * @return
     */
    @PostMapping("/image")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file){
        return ResponseEntity.ok(uploadService.uploadImage(file));
    }
}
