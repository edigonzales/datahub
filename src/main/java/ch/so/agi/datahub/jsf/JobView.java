package ch.so.agi.datahub.jsf;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.so.agi.datahub.model.JobResponse;
import ch.so.agi.datahub.service.JobResponseService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named
@RequestScoped
public class JobView {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private JobResponseService jobResponseService;

    public JobView(JobResponseService jobResponseService) {
        this.jobResponseService = jobResponseService;
    }
    
    @PostConstruct
    public void init() {
        System.out.println("** PostConstruct **");
    }
    
    public List<JobResponse> getJobs() {
        List<JobResponse> jobResponseList = jobResponseService.getJobsResponse();
        return jobResponseList;
    }
    
}
