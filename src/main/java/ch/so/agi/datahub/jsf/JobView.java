package ch.so.agi.datahub.jsf;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.so.agi.datahub.model.JobResponse;
import ch.so.agi.datahub.service.JobResponseService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
//@ViewScoped
@RequestScoped
public class JobView {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private LazyDataModel<JobResponse> lazyModel;
    
    private JobResponseService jobResponseService;
    
    public JobView(JobResponseService jobResponseService) {
        lazyModel = new JobLazyDataModel(jobResponseService);
        this.jobResponseService = jobResponseService;
    }
    
    @PostConstruct
    public void init() {}
        
    public LazyDataModel<JobResponse> getModel() {  
        return lazyModel;
    }
    
    public List<String> getOrganisations() {
        List<String> organisations = jobResponseService.getJobResponseList().stream()
            .map(o -> {
                return o.getOrganisation();
            })
            .distinct()
            .collect(Collectors.toList());
               
        Collections.sort(organisations);
        return organisations;
    }    
}
