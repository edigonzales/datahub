package ch.so.agi.datahub.service;

import java.io.File;
import java.io.FileInputStream;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ch.ehi.basics.settings.Settings;
import ch.so.agi.datahub.cayenne.DeliveriesAsset;
import ch.so.agi.datahub.cayenne.DeliveriesDelivery;

@Service
public class DeliveryService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.workDirectory}")
    private String workDirectory;
    
    @Value("${app.folderPrefix}")
    private String folderPrefix;

    @Value("${app.targetDirectory}")
    private String targetDirectory;
    
    @Value("${app.preferredIliRepo}")
    private String preferredIliRepo;

    private FilesStorageService filesStorageService;
    
    private ObjectContext objectContext;
    
    private EmailService emailService;
    
    public DeliveryService(FilesStorageService filesStorageService, ObjectContext objectContext, EmailService emailService) {
        this.filesStorageService = filesStorageService;
        this.objectContext = objectContext;
        this.emailService = emailService;
    }

    @Job(name = "Delivery", retries=0)
    public synchronized void deliver(JobContext jobContext, String email, String theme, String fileName, String config, String metaConfig, String host) throws IOException {                
        String jobId = jobContext.getJobId().toString();
        
//        try {
//            Thread.sleep(120000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // Validate file.
        Resource resource = filesStorageService.load(fileName, jobId, folderPrefix, workDirectory);        
        File transferFile = resource.getFile();
        logger.debug("<{}> transfer file: {}", jobId, transferFile.getAbsolutePath().toString());
        
        File logFile = Paths.get(transferFile.getAbsoluteFile().getParent(), jobId + ".log").toFile();
        String logFileName = logFile.getAbsolutePath();                
        logger.debug("<{}> log file name: {}", jobId, logFileName);
        
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
        
        logger.info("<{}> Validation start", jobId);
        boolean valid = Validator.runValidation(transferFile.getAbsolutePath().toString(), settings);
        logger.info("<{}> Validation end", jobId);
        
        // Copy file ("deliver") to target directory.
        boolean delivered;
        // TODO: nicht sicher, ob man das so will. Resp. für den Datenlieferanten ist nach Validierung fertig.
        // D.h. er kann nichts dafür, wenn es beim Kopieren einen Fehler gibt.
         
        try {
            filesStorageService.save(new FileInputStream(transferFile), transferFile.getName(), theme, null, targetDirectory);            
            filesStorageService.save(new FileInputStream(new File(logFileName)), transferFile.getName()+".log", theme, null, targetDirectory);            
            delivered = true;
        } catch (IOException e) {
            delivered = false;
        }
        
        // Update tables in database.
        updateDelivery(jobId, valid, delivered, logFile);   
        logger.info("<{}> File delivered", jobId);
        
        // Send email to delivery organisation
        // HTML content: https://stackoverflow.com/questions/5289849/how-do-i-send-html-email-in-spring-mvc
        String linkLogFile = "<a href='"+host+"/api/logs"+jobId+"'>"+jobId+"</a>";
        try {
            emailService.send(email, "datahub; " + jobId, "Validation: " + valid + "\n" + "Delivery: " + delivered + "\n" 
                    + "Logdatei: " + host + "/api/logs/" + jobId + "\n" + "Jobs: " + host + "/web/jobs");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("<{}> Error while sending email: {}", jobId, e.getMessage());
        }
    }
    
    private void updateDelivery(String jobId, boolean valid, boolean delivered, File logFile) {
        DeliveriesDelivery deliveriesDelivery = ObjectSelect.query(DeliveriesDelivery.class).where(DeliveriesDelivery.JOBID.eq(jobId)).selectOne(objectContext);
        deliveriesDelivery.setIsvalid(valid);
        deliveriesDelivery.setDelivered(delivered);
        
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
