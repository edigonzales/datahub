package ch.so.agi.datahub.controller;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ch.so.agi.datahub.model.Delivery;
import ch.so.agi.datahub.model.OperatDeliveryInfo;
import ch.so.agi.datahub.service.IlivalidatorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class DeliveryController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.dbSchema}")
    private String dbSchema;

    private JobScheduler jobScheduler;
    
    private StorageProvider storageProvider;

    private IlivalidatorService ilivalidatorService;
    
    private JdbcClient jdbcClient;

    public DeliveryController(JobScheduler jobScheduler, StorageProvider storageProvider, IlivalidatorService ilivalidatorService, JdbcClient jdbcClient) {
        this.jobScheduler = jobScheduler;
        this.storageProvider = storageProvider;
        this.ilivalidatorService = ilivalidatorService;
        this.jdbcClient = jdbcClient;
    }

    @Transactional(rollbackFor={InvalidDataAccessResourceUsageException.class})
    @PostMapping(value="/api/v1/deliveries", consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadFiles(@RequestPart(name = "theme", required = true) String theme,
            @RequestPart(name = "operat", required = true) String operat,
            @RequestPart(name = "file", required = true) MultipartFile[] file, 
            HttpServletRequest request) throws Exception {
        
        logger.info("****** do ilivalidation of delivery...");
        
        
        // Datei umbenennen hier?
        // Und abspeichern wo?
        
        // In Service dann die Resultate in die DB schreiben?

        
        // JobId für Jobrunr
        UUID jobIdUuid = UUID.randomUUID();
        String jobId = jobIdUuid.toString();
        
        // Die Delivery-Tabellen nachführen.
        OperatDeliveryInfo operatDeliveryInfo = (OperatDeliveryInfo) request.getAttribute("operat_delivery_info");
        
        jdbcClient.sql("INSERT INTO "+dbSchema+".deliveries_delivery(jobid, deliverydate, operat_r, user_r) VALUES (?, ?, ?, ?)")
                .params(List.of(jobIdUuid, new Date(), operatDeliveryInfo.operatpk(), operatDeliveryInfo.userpk()))
                .update();
            
        // Primary Key des neuen, vorhin erzeugten Records eruieren, damit die Kindtabelle befüllt werden kann.  
        String stmt = """
SELECT 
    t_id AS tid,
    jobid,
    deliverydate,
    operat_r AS operatfk,
    user_r AS userfk
FROM 
    %s.deliveries_delivery
WHERE 
    jobid = :jobid
;
                    """.formatted(dbSchema);

        Optional<Delivery> deliveryOptional = jdbcClient.sql(stmt)
                .param("jobid", jobIdUuid)
                //.param("jobid", UUID.randomUUID())
                .query(Delivery.class).optional();        
        
        if (deliveryOptional.isEmpty()) {
            throw new InvalidDataAccessResourceUsageException("<"+jobId+"> JobId not found."); // Rollback der Transaktion.
        }             
      
        // Update Tabelle deliveries_asset.
        int deliveryTid = deliveryOptional.get().tid();
        jdbcClient.sql("INSERT INTO "+dbSchema+".deliveries_asset(originalfilename, sanitizedfilename, atype, delivery_r) VALUES (?, ?, ?, ?)")
                .params(List.of("foo", "bar", "PrimaryData", deliveryTid))
                .update();            
       
        // Validierungsjob in Jobrunr queuen
        jobScheduler.enqueue(jobIdUuid, () -> ilivalidatorService.validate());
        logger.info("<{}> Job is being queued for validation.", jobId);
        
        return ResponseEntity
                .accepted()
                .header("Operation-Location", getHost()+"/api/v1/jobs/"+jobId)
                .body(null);
    }
    
    @ExceptionHandler({SQLException.class, InvalidDataAccessResourceUsageException.class})
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
