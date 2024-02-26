package ch.so.agi.datahub.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
    public void save(InputStream is, String fileName, String parentDir, String prefix, String rootDir) throws IOException {
        Path rootDirectoryPath = Paths.get(rootDir);
        
        prefix = prefix==null?"":prefix;
        Path fileDirectoryPath = rootDirectoryPath.resolve(prefix + parentDir);

        try {
            Files.createDirectories(fileDirectoryPath); // does not throw exception if directory exists
            Files.copy(is, fileDirectoryPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING
                    );
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException();
        } 
    }

    @Override
    public Resource load(String fileName, String parentDir, String prefix, String rootDir) throws IOException {
        Path rootDirectoryPath = Paths.get(rootDir);

        prefix = prefix==null?"":prefix;
        Path fileDirectoryPath = rootDirectoryPath.resolve(folderPrefix + parentDir);

        try {
            Path path = fileDirectoryPath.resolve(fileName);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new IOException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new IOException("Error: " + e.getMessage());
        }
    }
}
