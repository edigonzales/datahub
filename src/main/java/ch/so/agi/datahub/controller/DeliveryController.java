package ch.so.agi.datahub.controller;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.StorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ch.so.agi.datahub.AppConstants;
import ch.so.agi.datahub.cayenne.CoreOperat;
import ch.so.agi.datahub.cayenne.CoreUser;
import ch.so.agi.datahub.cayenne.DeliveriesAsset;
import ch.so.agi.datahub.cayenne.DeliveriesDelivery;
import ch.so.agi.datahub.service.DeliveryService;
import ch.so.agi.datahub.service.FilesStorageService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class DeliveryController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Value("${app.workDirectory}")
    private String workDirectory;
    
    @Value("${app.folderPrefix}")
    private String folderPrefix;
    
    private JobScheduler jobScheduler;
    
    private StorageProvider storageProvider;

    private DeliveryService deliveryService;
        
    private ObjectContext objectContext;
    
    private FilesStorageService filesStorageService;

    public DeliveryController(JobScheduler jobScheduler, StorageProvider storageProvider,
            DeliveryService deliveryService, ObjectContext objectContext, FilesStorageService filesStorageService) {
        this.jobScheduler = jobScheduler;
        this.storageProvider = storageProvider;
        this.deliveryService = deliveryService;
        this.objectContext = objectContext;
        this.filesStorageService = filesStorageService;
    }

    @Transactional(rollbackFor={InvalidDataAccessResourceUsageException.class})
    @PostMapping(value="/api/v1/deliveries", consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadFile(@RequestPart(name = "theme", required = true) String theme,
            @RequestPart(name = "operat", required = true) String operat,
            @RequestPart(name = "file", required = true) MultipartFile file, 
            HttpServletRequest request) throws Exception {
        
        logger.info("********* DELIVERY CONTROLLER HERE...");
        
       
        // TODO
        // - In Service dann die Resultate in die DB schreiben?
        // - Wann / wie / wohin schreiben für "Publikation". FilesStorageService: save jobId -> (sub)directory
        // Muss sonst noch was ändern? Ah, WorkDirectory als weitere Parameter, das targetDirectory.
        // Am besten gleich mit S3 durchspielen, dann muss ich mich beim Interface auch darum kümmern.
        
        // JobId für Jobrunr
        UUID jobIdUuid = UUID.randomUUID();
        String jobId = jobIdUuid.toString();
  
        // Get the operat/theme information we gathered in the authorization filter
        DataRow operatDeliveryInfo = (DataRow) request.getAttribute(AppConstants.ATTRIBUTE_OPERAT_DELIVERY_INFO);

        // Normalize file name
        String originalFileName = file.getOriginalFilename();
        String sanitizedFileName = (String)operatDeliveryInfo.get("operatid") + ".xtf";

        // Daten speichern
        filesStorageService.save(file.getInputStream(), sanitizedFileName, jobId, folderPrefix, workDirectory);
        
        // Die Delivery-Tabellen nachführen.
        long operatTid = (Long)operatDeliveryInfo.get("operattid");
        long userTid = (Long)operatDeliveryInfo.get("usertid");
        
        // Jobrunr kann nicht mit null Strings umgehen.
        String validatorConfig = operatDeliveryInfo.get("config")!=null?(String)operatDeliveryInfo.get("config"):""; 
        String validatorMetaConfig = operatDeliveryInfo.get("metaconfig")!=null?(String)operatDeliveryInfo.get("metaconfig"):"";
                        
        CoreOperat coreOperat = SelectById.query(CoreOperat.class, operatTid).selectOne(objectContext);
        CoreUser coreUser = SelectById.query(CoreUser.class, userTid).selectOne(objectContext);
                
        DeliveriesAsset deliveriesAsset = objectContext.newObject(DeliveriesAsset.class);
        deliveriesAsset.setAtype("PrimaryData");
        deliveriesAsset.setOriginalfilename(originalFileName);
        deliveriesAsset.setSanitizedfilename(sanitizedFileName);
        
        DeliveriesDelivery deliveriesDelivery = objectContext.newObject(DeliveriesDelivery.class);
        deliveriesDelivery.setJobid(jobId);
        deliveriesDelivery.setDeliverydate(LocalDateTime.now());
        
        deliveriesDelivery.setCoreOperat(coreOperat);
        //deliveriesDelivery.setCoreUser(coreUser);
        deliveriesDelivery.addToDeliveriesAssets(deliveriesAsset);
                      
        // Validierungsjob in Jobrunr queuen.
        jobScheduler.enqueue(jobIdUuid, () -> deliveryService.deliver(JobContext.Null, theme, sanitizedFileName,
                validatorConfig, validatorMetaConfig));
        logger.info("<{}> Job is being queued for validation.", jobId);
       
        objectContext.commitChanges();

        return ResponseEntity
                .accepted()
                .header("Operation-Location", getHost()+"/api/v1/jobs/"+jobId)
                .body(null);
    }
    
    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<?> databaseError(Exception e) {
        logger.error("<{}>", e.getMessage());
        return ResponseEntity
                .internalServerError()
                .body("Please contact service provider. Delivery is not queued.");
    }
    
    private String getHost() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }
}
