package com.yan.crm_project.service;

import java.io.*;

import org.springframework.web.multipart.*;

/**
 * Tom tat: Quan ly vong doi file upload avatar tam thoi.
 */
public interface FileUploadService {
    void init();

    File upload(MultipartFile file);

    void remove(String fileName);
}
