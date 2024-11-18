package com.rentalcar.rentalcar.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileStorageService {
    String storeFile(MultipartFile file, Path customPath, String fileName) ;


    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteFolder(Path folderPath);

    void changeFolderName(String oldFolderName, String newFolderName);

    boolean moveFiles(Path sourceFolder, Path destinationFolder);

    void deleteFile(Path filePath);

}
