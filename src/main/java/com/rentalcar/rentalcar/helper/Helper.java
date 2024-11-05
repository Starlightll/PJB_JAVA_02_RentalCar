package com.rentalcar.rentalcar.helper;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public class Helper {

    //Save file
    public void saveFile(MultipartFile file, Path folder, String fileName) throws IOException {
        if (file != null && !file.isEmpty()) {
            Path filePath = folder.resolve(fileName + "-" + System.currentTimeMillis() + "." + FilenameUtils.getExtension(file.getOriginalFilename()));
            file.transferTo(filePath);
        }
    }
}
