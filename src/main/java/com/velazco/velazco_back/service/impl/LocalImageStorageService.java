package com.velazco.velazco_back.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.velazco.velazco_back.exceptions.FileTooLargeException;
import com.velazco.velazco_back.service.ImageStorageService;

@Service
public class LocalImageStorageService implements ImageStorageService {

    private static final long MAX_SIZE = 4 * 1024 * 1024;
    
    @Value("${storage.upload-dir:uploads}")
    private String uploadDir;
    
    private static final String URL_PREFIX = "/storage/";

    @Override
    public void validateSize(MultipartFile image) {
        if (image.getSize() > MAX_SIZE) {
            throw new FileTooLargeException("La imagen no debe superar los 4 MB.");
        }
    }

    @Override
    public String store(MultipartFile image) {
        try {
            String filename = UUID.randomUUID() + "_" + Paths.get(image.getOriginalFilename()).getFileName();
            Path path = Paths.get(uploadDir).resolve(filename);
            Files.createDirectories(path.getParent());
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return URL_PREFIX + filename;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen", e);
        }
    }

    @Override
    public void delete(String imagePath) {
        if (imagePath == null || imagePath.isEmpty())
            return;
        try {
            Path filePath = Paths.get(uploadDir).resolve(Paths.get(imagePath).getFileName().toString());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("No se pudo eliminar la imagen anterior: " + e.getMessage());
        }
    }
}
