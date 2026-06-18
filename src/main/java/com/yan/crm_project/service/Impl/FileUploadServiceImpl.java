package com.yan.crm_project.service.Impl;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.springframework.stereotype.*;
import org.springframework.web.multipart.*;

import com.yan.crm_project.service.*;

import lombok.extern.slf4j.*;

import static com.yan.crm_project.constant.AttributeConstant.*;
import static java.nio.file.Files.*;

@Service
@Slf4j
/**
 * Tom tat: Luu file avatar tam thoi vao thu muc cau hinh va xoa file sau khi
 * resize thanh cong.
 */
public class FileUploadServiceImpl implements FileUploadService {
    @Override
    public void init() {
        var folderPath = Paths.get(AVATAR_PATH).toAbsolutePath().normalize();
        if (!exists(folderPath)) {
            try {
                createDirectories(folderPath);
            } catch (IOException e) {
                throw new RuntimeException("Could not initialize storage!");
            }
        }
    }

    @Override
    public File upload(MultipartFile file) {
        try {
            var folderPath = Paths.get(AVATAR_PATH).toAbsolutePath().normalize();
            createDirectories(folderPath);
            var originalName = org.springframework.util.StringUtils
                    .cleanPath(Optional.ofNullable(file.getOriginalFilename()).orElse("avatar"));
            if (originalName.contains("..")) {
                log.warn("Rejected unsafe upload filename: {}", originalName);
                return null;
            }

            var filePath = folderPath.resolve(AVATAR_PREFIX + originalName).normalize();
            if (!filePath.startsWith(folderPath)) {
                log.warn("Rejected upload outside avatar folder: {}", originalName);
                return null;
            }

            write(filePath, file.getBytes());
            return filePath.toFile();
        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void remove(String fileName) {
        var folderPath = Paths.get(AVATAR_PATH).toAbsolutePath().normalize();
        var filePath = folderPath.resolve(fileName).normalize();
        if (!filePath.startsWith(folderPath)) {
            log.warn("Rejected delete outside avatar folder: {}", fileName);
            return;
        }

        if (exists(filePath)) {
            try {
                delete(filePath);
            } catch (IOException e) {
                log.error("Error deleting file: {}", e.getMessage());
            }
        }
    }
}
