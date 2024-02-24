package ch.so.agi.datahub.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FilesStorageService {
    public void init();

    public void save(MultipartFile file, String sanitizedFileName, String jobId);

    public Resource load(String fileName, String jobid);
}
