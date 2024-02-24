package ch.so.agi.datahub.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.interlis2.validator.Validator;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.context.JobContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import ch.ehi.basics.settings.Settings;

@Service
public class IlivalidatorService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.preferredIliRepo}")
    private String preferredIliRepo;

    private FilesStorageService filesStorageService;
    
    public IlivalidatorService(FilesStorageService filesStorageService) {
        this.filesStorageService = filesStorageService;
    }
    
    // S3-Support:
    // Es wird eine zweiter Prefix ("validation_") o.ä. benötigt.
    // Die Resource wird dorthin kopiert. 

    @Job(name = "Ilivalidator", retries=0)
    public synchronized boolean validate(JobContext jobContext, String fileName, String config, String metaConfig) throws IOException {
        logger.info("********* VALIDATING...");
        logger.info(jobContext.getJobId().toString());
        
        logger.debug(config);
        logger.debug(metaConfig);
        
        String jobId = jobContext.getJobId().toString();
        
        
        Resource resource = filesStorageService.load(fileName, jobId);        
        File transferFile = resource.getFile();
        logger.debug(transferFile.getAbsolutePath().toString());
        
        File logFile = Paths.get(transferFile.getAbsoluteFile().getParent(), transferFile.getName() + ".log").toFile();
        String logFileName = logFile.getAbsolutePath();                
        logger.debug(logFileName);
        
        Settings settings = new Settings();
        settings.setValue(Validator.SETTING_LOGFILE, logFileName);
        settings.setValue(Validator.SETTING_XTFLOG, logFileName + ".xtf");
        settings.setValue(Validator.SETTING_ILIDIRS, preferredIliRepo+";"+Validator.SETTING_DEFAULT_ILIDIRS);

        if (config != null) {
            settings.setValue(Validator.SETTING_CONFIGFILE, "ilidata:" + config);
        }

        if (metaConfig != null) {
            settings.setValue(Validator.SETTING_META_CONFIGFILE, "ilidata:" + metaConfig);
        }

        logger.info("<{}> Validation start", jobId);
        boolean valid = Validator.runValidation(transferFile.getAbsolutePath().toString(), settings);
        logger.info("<{}> Validation end", jobId);
        
        return valid;
    }
}
