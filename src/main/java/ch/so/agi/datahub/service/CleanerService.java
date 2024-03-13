package ch.so.agi.datahub.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ConditionalOnProperty(
        value="app.cleanerEnabled", 
        havingValue = "true", 
        matchIfMissing = false)
@Service
public class CleanerService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${app.workDirectory}")
    private String workDirectory;
    
    @Value("${app.folderPrefix}")
    private String folderPrefix;

    private FilesStorageService storageService;
       
    public CleanerService(FilesStorageService storageService) {
        this.storageService = storageService;
    }

    @Async("asyncTaskExecutor")
    @Scheduled(cron="0 */30 * * * *")
    //@Scheduled(fixedRate = 1 * 30 * 1000) /* Runs every 30 seconds */
    public void cleanUp() {    
        long deleteFileAge = 60*60*24;
        log.info("Deleting files from previous delivery runs older than {} [s]...", deleteFileAge);

        if (storageService instanceof LocalFilesStorageService) {
            java.io.File[] tmpDirs = new java.io.File(workDirectory).listFiles();
            if(tmpDirs!=null) {
                for (java.io.File tmpDir : tmpDirs) {
                    if (tmpDir.getName().startsWith(folderPrefix)) {
                        try {
                            FileTime creationTime = (FileTime) Files.getAttribute(Paths.get(tmpDir.getAbsolutePath()), "creationTime");                    
                            Instant now = Instant.now();
                            
                            long fileAge = now.getEpochSecond() - creationTime.toInstant().getEpochSecond();
                            log.trace("found folder with prefix: {}, age [s]: {}", tmpDir, fileAge);

                            if (fileAge > deleteFileAge) {
                                log.debug("deleting {}", tmpDir.getAbsolutePath());
                                FileSystemUtils.deleteRecursively(tmpDir);
                            }
                        } catch (IOException e) {
                            throw new IllegalStateException(e);
                        }
                    }
                }
            }            
        } 
    }
}
