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
import org.springframework.web.bind.annotation.RestController;

import ch.so.agi.datahub.model.JobResponse;
import ch.so.agi.datahub.service.JobResponseService;

@Controller
public class JobController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private JobResponseService jobResponseService;
    
    public JobController(JobResponseService jobResponseService) {
        this.jobResponseService = jobResponseService;
    }
    
    @GetMapping(path = "/api/v1/jobs")
    public ResponseEntity<?> getJobsApi(Authentication authentication) {
        List<JobResponse> jobResponseList = jobResponseService.getJobsByOrg(authentication.getName());
        
        if (jobResponseList.size() == 0) {
            return new ResponseEntity<List<JobResponse>>(null, null, HttpStatus.NO_CONTENT);
        }
        
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<List<JobResponse>>(jobResponseList, responseHeaders, HttpStatus.OK);
    }
        
    @GetMapping(path = "/web/jobs")
    public String getJobsWeb(Authentication authentication, Model model) {
        List<JobResponse> jobResponseList = jobResponseService.getJobsByOrg(authentication.getName());
        
//        if (jobResponseList.size() == 0) {
//            return new ResponseEntity<List<JobResponse>>(null, null, HttpStatus.NO_CONTENT);
//        }
        
        model.addAttribute("message", "Hello World!");
        return "jobs";

        
//        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
//        return new ResponseEntity<List<JobResponse>>(jobResponseList, responseHeaders, HttpStatus.OK);
    }
    
    
    @GetMapping(path = "/api/v1/jobs/{jobId}")
    public ResponseEntity<?> getJobById(Authentication authentication, 
            Model model,
            @RequestHeader(value = "Accept") String acceptHeader,
            @PathVariable("jobId") String jobId) throws IOException {
        
        JobResponse jobResponse = jobResponseService.getJobResponseById(jobId, authentication.getName());
//        logger.info(jobResponse.toString());
        
//        model.addAttribute("message", "Hello World!");
//        return "foo";

        if (acceptHeader != null && acceptHeader.contains(MediaType.TEXT_HTML_VALUE)) {
            // HTML response
//            ModelAndView modelAndView = new ModelAndView("templateName");
//            modelAndView.addObject("key", "value");
//            return modelAndView;
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.TEXT_HTML);
            return new ResponseEntity<String>("Ich bin HTML", responseHeaders, HttpStatus.OK);
        } else {
            // JSON response
//            return new ModelAndView().addObject("key", "value");
            
            if (jobResponse == null) {
                return new ResponseEntity<JobResponse>(null, null, HttpStatus.NO_CONTENT);
            }
            
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<JobResponse>(jobResponse, responseHeaders, HttpStatus.OK);
        }        
    }
}
