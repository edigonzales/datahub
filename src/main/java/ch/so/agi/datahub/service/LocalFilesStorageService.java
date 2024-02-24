package ch.so.agi.datahub.service;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFilesStorageService implements FilesStorageService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.workDirectory}")
    private String workDirectory;
    
    @Value("${app.folderPrefix}")
    private String folderPrefix;

    @Override
    public void init() {
    }

    @Override
    public void save(MultipartFile file, String sanitizedFileName, String jobId) {
        logger.debug("Work directory: {}", workDirectory);
        Path workDirectoryPath = Paths.get(workDirectory);
        Path jobDirectoryPath = workDirectoryPath.resolve(folderPrefix + jobId);
        
        try {
            Files.createDirectory(jobDirectoryPath);
            Files.copy(file.getInputStream(), jobDirectoryPath.resolve(sanitizedFileName));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        } 
    }

    @Override
    public Resource load(String fileName, String jobId) {
        logger.debug("Work directory: {}", workDirectory);
        Path workDirectoryPath = Paths.get(workDirectory);
        Path jobDirectoryPath = workDirectoryPath.resolve(folderPrefix + jobId);

        try {
            Path path = jobDirectoryPath.resolve(fileName);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

}
