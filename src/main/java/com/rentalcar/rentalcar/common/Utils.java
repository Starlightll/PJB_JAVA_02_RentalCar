package com.rentalcar.rentalcar.common;

import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

public class Utils {

    public String getExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            return fileName.substring(index + 1);
        }
        return null;
    }

    public String getExtension(MultipartFile file) {
        return getExtension(Objects.requireNonNull(file.getOriginalFilename()));
    }
}
