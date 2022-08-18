package com.yan.crm_project.service;

import java.io.*;

import org.springframework.web.multipart.*;

public interface FileUploadService {
    public void init();

    public File upload(MultipartFile file);

    public void remove(String fileName);
}
