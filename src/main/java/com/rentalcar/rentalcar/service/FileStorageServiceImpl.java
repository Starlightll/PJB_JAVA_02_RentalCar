package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.configs.FileStorageProperties;
import com.rentalcar.rentalcar.exception.FileNotFoundException;
import com.rentalcar.rentalcar.exception.FileStorageException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    private final Path fileStorageLocation;


    @Autowired
    public FileStorageServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, Path customPath, String fileName) {
        try {
            // Create the custom directory if it doesn't exist
            if (!Files.exists(customPath)) {
                Files.createDirectories(customPath);
            }


            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Create the complete path including the file
            Path targetLocation = customPath.resolve(fileName);

            // Copy file to the target location
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return targetLocation.toString();
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file. Please try again!", ex);
        }
    }

    @Override
    public String storeFile(File file, Path customPath, String fileName) {
        try {
            // Create the custom directory if it doesn't exist
            if (!Files.exists(customPath)) {
                Files.createDirectories(customPath);
            }

            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Create the complete path including the file
            Path targetLocation = customPath.resolve(fileName);

            // Copy file to the target location
            Files.copy(file.toPath(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return targetLocation.toString();
        } catch (IOException ex) {
            //Delete the created folder
            try {
                FileUtils.deleteDirectory(new File(customPath.toString()));
            } catch (IOException e) {
                throw new FileStorageException("Could not delete folder", e);
            }
            throw new FileStorageException("Could not store file. Please try again!", ex);
        }
    }

    @Override
    public Path load(String filename) {
        return fileStorageLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileStorageException(
                        "Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new FileStorageException("Could not read file: " + filename, e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFolder(Path folderPath) {
        //Delete the folder
        try {
            FileUtils.deleteDirectory(new File(folderPath.toString()));
        } catch (IOException e) {
            throw new FileStorageException("Could not delete folder", e);
        }
    }


    @Override
    public void changeFolderName(String oldFolderName, String newFolderName) {
    }


    @Override
    public boolean moveFiles(Path sourceDir, Path targetDir) {
        try {
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir);
            for (Path file : stream) {
                Path targetPath = targetDir.resolve(file.getFileName());
                Files.move(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("Files moved successfully.");
            return true;
        } catch (IOException e) {
            //Delete the created folder
            try {
                FileUtils.deleteDirectory(new File(targetDir.toString()));
            } catch (IOException ex) {
                throw new FileStorageException("Could not delete folder", ex);
            }
            System.err.println("Error when moving files: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean moveFilesWithOutDelete(Path sourceFolder, Path destinationFolder) {
        try {
            if (!Files.exists(destinationFolder)) {
                Files.createDirectories(destinationFolder);
            }

            DirectoryStream<Path> stream = Files.newDirectoryStream(sourceFolder);
            for (Path file : stream) {
                Path targetPath = destinationFolder.resolve(file.getFileName());
                Files.move(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("Files moved successfully.");
            return true;
        } catch (IOException e) {
            System.err.println("Error when moving files: " + e.getMessage());
            return false;
        }
    }

    public boolean copyFiles(Path sourceFolder, Path destinationFolder) {
        try {
            if (!Files.exists(destinationFolder)) {
                Files.createDirectories(destinationFolder);
            }

            //Check if the source folder is Exist
            if (Files.exists(sourceFolder)) {
                DirectoryStream<Path> stream = Files.newDirectoryStream(sourceFolder);
                for (Path file : stream) {
                    Path targetPath = destinationFolder.resolve(file.getFileName());
                    Files.copy(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            System.out.println("Files copied successfully.");
            return true;
        } catch (IOException e) {
            //Delete the created folder
            try {
                FileUtils.deleteDirectory(new File(destinationFolder.toString()));
            } catch (IOException ex) {
                throw new FileStorageException("Could not delete folder", ex);
            }
            System.err.println("Error when copying files: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void deleteFile(Path filePath) {
        try {
            //Check if the file exists
            if (!Files.exists(filePath)) {
                return;
            }
            Files.delete(filePath);
        } catch (IOException e) {
            throw new FileStorageException("Could not delete file", e);
        }
    }

}
