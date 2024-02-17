package ch.so.agi.datahub.service;

import org.jobrunr.jobs.annotations.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IlivalidatorService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Job(name = "Ilivalidator", retries=0)
    public synchronized boolean validate() {
        
        // JobContext um JobId zu bekommen?
        
        logger.info("** VALIDATING **");
        
        return true;
    }
}
