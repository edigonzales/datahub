package ch.so.agi.datahub.controller;

import java.util.UUID;

import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.so.agi.datahub.service.IlivalidatorService;

@RestController
public class DeliveryController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private JobScheduler jobScheduler;

    private IlivalidatorService ilivalidatorService;

    public DeliveryController(JobScheduler jobScheduler, IlivalidatorService ilivalidatorService) {
        this.jobScheduler = jobScheduler;
        this.ilivalidatorService = ilivalidatorService;
    }

    @PostMapping(value="/api/v1/deliveries", consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadFiles(@RequestPart(name = "theme", required = true) String theme,
            @RequestPart(name = "operat", required = true) String operat,
            @RequestPart(name = "file", required = true) MultipartFile[] file) {

        
        logger.info("****** do validation of delivery...");
        logger.info(jobScheduler.toString());

        UUID jobIdUuid = UUID.randomUUID();
        String jobId = jobIdUuid.toString();

        jobScheduler.enqueue(jobIdUuid, () -> ilivalidatorService.validate());
        logger.debug("<{}> Job is being queued", jobId);
        
        // path.toUri().toString() sollte "file://" liefern. Vielleicht sowas.
        
        
        return ResponseEntity
                .accepted()
                //.header("Operation-Location", getHost()+"/api/jobs/"+jobId)
                .body("Hallo Welt.");
    }
}
