package com.yan.crm_project.service.Impl;

import java.io.*;

import org.springframework.stereotype.*;

import com.yan.crm_project.service.*;

import lombok.extern.slf4j.*;

import static com.yan.crm_project.constant.ApplicationConstant.*;
import static com.yan.crm_project.constant.AttributeConstant.*;
import static java.nio.file.Paths.*;
import static javax.imageio.ImageIO.*;
import static org.imgscalr.Scalr.*;
import static org.imgscalr.Scalr.Method.*;
import static org.imgscalr.Scalr.Mode.*;

@Service
@Slf4j
/**
 * Tom tat: Resize avatar ve anh JPG vuong kich thuoc AVATAR_SIZE.
 */
public class ImageServiceImpl implements ImageService {
    @Override
    public boolean resizeImage(File file, String name) {
        try {
            var inputImg = read(file);
            if (inputImg == null) {
                return false;
            }

            var outputImg = resize(inputImg, ULTRA_QUALITY, FIT_EXACT, AVATAR_SIZE);
            write(outputImg, "jpg", get(AVATAR_PATH, name).toFile());
            outputImg.flush();
            return true;
        } catch (Exception e) {
            log.error("Error resizing image: {}", e.getMessage());
            return false;
        }
    }
}
