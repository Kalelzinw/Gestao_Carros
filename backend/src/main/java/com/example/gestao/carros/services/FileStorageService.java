package com.example.gestao.carros.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Não foi possível criar o diretório para salvar os arquivos.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        try {
            if(uniqueFileName.contains("..")) {
                throw new RuntimeException("Desculpe! O nome do arquivo contém uma sequência de caminho inválida " + uniqueFileName);
            }
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return uniqueFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Não foi possível salvar o arquivo " + uniqueFileName + ". Por favor, tente novamente!", ex);
        }
    }
    
    public void deleteFile(String fileName) {
        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(targetLocation);
        } catch (IOException ex) {
            // Lançar uma exceção ou apenas logar um aviso é uma opção.
            System.err.println("Não foi possível deletar o arquivo " + fileName + ". " + ex.getMessage());
        }
    }
}