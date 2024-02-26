package ch.so.agi.datahub.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FilesStorageService {
    public void init();

    public void save(InputStream is, String fileName, String parentDir, String prefix, String rootDir) throws IOException;

    public Resource load(String fileName, String parentDir, String prefix, String rootDir) throws IOException;
}
