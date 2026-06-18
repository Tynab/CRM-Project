package com.yan.crm_project.service;

import java.io.*;

/**
 * Tom tat: Xu ly resize anh dai dien ve kich thuoc chuan cua he thong.
 */
public interface ImageService {
    boolean resizeImage(File file, String name);
}
