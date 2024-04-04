package ch.so.agi.datahub.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import ch.so.agi.datahub.model.JobResponse;
import ch.so.agi.datahub.service.JobResponseService;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class JobController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private JobResponseService jobResponseService;
    
    public JobController(JobResponseService jobResponseService) {
        this.jobResponseService = jobResponseService;
    }
    
    @GetMapping(path = "/api/jobs")
    public ResponseEntity<?> getJobsApi() {
        List<JobResponse> jobResponseList = jobResponseService.getJobsResponse();
        
        if (jobResponseList.size() == 0) {
            return new ResponseEntity<List<JobResponse>>(null, null, HttpStatus.NO_CONTENT);
        }
        
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<List<JobResponse>>(jobResponseList, responseHeaders, HttpStatus.OK);
    }
        
    @GetMapping(path = "/web.old/jobs")
    public String getJobsWeb(Model model, HttpServletResponse response) {
        List<JobResponse> jobResponseList = jobResponseService.getJobsResponse();
        model.addAttribute("jobResponseList", jobResponseList);
        
        response.setHeader("Refresh", "15");
        
        return "jobs";
    }

    @GetMapping(path = "/api/jobs/{jobId}")
    public ResponseEntity<?> getJobApiById(Model model, @PathVariable("jobId") String jobId) throws IOException {
        JobResponse jobResponse = jobResponseService.getJobResponseById(jobId);
            
        if (jobResponse == null) {
            return new ResponseEntity<JobResponse>(null, null, HttpStatus.NO_CONTENT);
        }
            
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<JobResponse>(jobResponse, responseHeaders, HttpStatus.OK);
    }
    
    @GetMapping(path = "/web/jobs.old/{jobId}")
    public ResponseEntity<?> getJobWebById(@PathVariable("jobId") String jobId) throws IOException {
        JobResponse jobResponse = jobResponseService.getJobResponseById(jobId);
        
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<JobResponse>(jobResponse, responseHeaders, HttpStatus.OK);
    }
}
