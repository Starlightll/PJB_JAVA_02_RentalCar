package com.rentalcar.rentalcar.configs;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file")
@Data
@NoArgsConstructor
public class FileStorageProperties {
    // file.upload-dir=./uploads --> uploadDir = ./uploads
    private String uploadDir;

}
