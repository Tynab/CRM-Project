package com.yan.crm_project.service.Impl;

import java.io.*;
import java.nio.file.*;

import org.springframework.stereotype.*;
import org.springframework.web.multipart.*;

import com.yan.crm_project.service.*;

import lombok.extern.slf4j.*;

import static com.yan.crm_project.constant.AttributeConstant.*;
import static java.nio.file.Files.*;

@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {
    @Override
    public void init() {
        var folderPath = Paths.get(AVATAR_PATH);
        // check exists path
        if (!exists(folderPath)) {
            try {
                createDirectory(folderPath);
            } catch (IOException e) {
                throw new RuntimeException("Could not initialize storage!");
            }
        }
    }

    @Override
    public File upload(MultipartFile file) {
        try {
            var filePath = Paths.get(AVATAR_PATH, AVATAR_PREFIX + file.getOriginalFilename());
            write(filePath, file.getBytes());
            return filePath.toFile();
        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void remove(String fileName) {
        var filePath = Paths.get(AVATAR_PATH, fileName);
        if (exists(filePath)) {
            try {
                delete(filePath);
            } catch (IOException e) {
                log.error("Error deleting file: {}", e.getMessage());
            }
        }
    }
}
