package ch.so.agi.datahub.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
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
            System.out.println(jobDirectoryPath.resolve(sanitizedFileName).toUri());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        } 
    }

    @Override
    public Resource load(String filename) {
        // TODO Auto-generated method stub
        return null;
    }

}
