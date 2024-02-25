package ch.so.agi.datahub.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.interlis2.validator.Validator;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.context.JobContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import ch.ehi.basics.settings.Settings;
import ch.so.agi.datahub.cayenne.DeliveriesAsset;
import ch.so.agi.datahub.cayenne.DeliveriesDelivery;

@Service
public class IlivalidatorService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.preferredIliRepo}")
    private String preferredIliRepo;

    private FilesStorageService filesStorageService;
    
    private ObjectContext objectContext;
    
    public IlivalidatorService(FilesStorageService filesStorageService, ObjectContext objectContext) {
        this.filesStorageService = filesStorageService;
        this.objectContext = objectContext;
    }
    
    // S3-Support:
    // Es wird eine zweiter Prefix ("validation_") o.ä. benötigt.
    // Die Resource wird dorthin kopiert. 

    
    // TODO:
    // - Umbennenen in DeliveryService. Methode "deliver"
    // - Dieser ruft ohne jobrunr einen IlivalidatorService auf.
    // - Zusätzlich gibt es noch einen ValidationService (für ohne Delivery). Dieser verwendet auch den IlivalidatorService.
    // - neue Attribut in Delivery-Class: isDelivered. Datei wurde am Zielort gespeichert. 
    
    
    @Job(name = "Ilivalidator", retries=0)
    public synchronized boolean validate(JobContext jobContext, String fileName, String config, String metaConfig) throws IOException {
        logger.info("********* VALIDATING...");
        logger.info(jobContext.getJobId().toString());
                
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
        settings.setValue(Validator.SETTING_CSVLOG, logFileName + ".csv");
        settings.setValue(Validator.SETTING_ILIDIRS, preferredIliRepo+";"+Validator.SETTING_DEFAULT_ILIDIRS);

        if (!config.isEmpty()) {
            settings.setValue(Validator.SETTING_CONFIGFILE, "ilidata:" + config);
        }

        if (!metaConfig.isEmpty()) {
            settings.setValue(Validator.SETTING_META_CONFIGFILE, "ilidata:" + metaConfig);
        }

//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {}
        
        logger.info("<{}> Validation start", jobId);
        boolean valid = Validator.runValidation(transferFile.getAbsolutePath().toString(), settings);
        logger.info("<{}> Validation end", jobId);
        
        updateDelivery(jobId, valid, logFile);
        
        return valid;
    }
    
    private void updateDelivery(String jobId, boolean valid, File logFile) {
        DeliveriesDelivery deliveriesDelivery = ObjectSelect.query(DeliveriesDelivery.class).where(DeliveriesDelivery.JOBID.eq(jobId)).selectOne(objectContext);
        deliveriesDelivery.setIsvalid(valid);
        
        List<String> extensions = List.of("", ".xtf", ".csv");
        for (String ext : extensions) {
            DeliveriesAsset deliveriesAsset = objectContext.newObject(DeliveriesAsset.class);
            deliveriesAsset.setAtype("ValidationReport");
            deliveriesAsset.setOriginalfilename(logFile.getName() + ext);
            deliveriesAsset.setSanitizedfilename(logFile.getName() + ext);
            deliveriesDelivery.addToDeliveriesAssets(deliveriesAsset);
        }

        objectContext.commitChanges();                
    }
}
