package ch.so.agi.datahub.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import ch.so.agi.datahub.service.FilesStorageService;

@Controller
public class LogController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.workDirectory}")
    private String workDirectory;
    
    @Value("${app.folderPrefix}")
    private String folderPrefix;

    private FilesStorageService filesStorageService;

    public LogController(FilesStorageService filesStorageService) {
        this.filesStorageService = filesStorageService;
    }
    
    @GetMapping(path = "/api/v1/logs/{jobId}")
    public ResponseEntity<?> getLogById(@PathVariable("jobId") String jobId) throws IOException {        
        return getLog(jobId);
    }
    
    private ResponseEntity<?> getLog(String jobId) throws IOException {
        Resource resource = filesStorageService.load(jobId+".log", jobId, folderPrefix, workDirectory);        
        Path logFile = resource.getFile().toPath();
        
        MediaType mediaType = new MediaType("text", "plain", StandardCharsets.UTF_8);
        return ResponseEntity.ok().header("Content-Type", "charset=utf-8")
                .contentLength(Files.size(logFile))
                .contentType(mediaType)
                .body(Files.readString(logFile));
    }
    
    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<?> error(Exception e) {
        logger.error("<{}>", e.getMessage());
        return ResponseEntity
                .internalServerError()
                .body("Please contact service provider. Error loading log file.");
    }
   
}
