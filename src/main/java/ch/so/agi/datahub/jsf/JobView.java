package ch.so.agi.datahub.jsf;

import java.util.List;

import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.so.agi.datahub.model.JobResponseBean;
import ch.so.agi.datahub.service.JobResponseBeanService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
//@ViewScoped
@RequestScoped
public class JobView {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private LazyDataModel<JobResponseBean> lazyModel;
    
    public JobView(JobResponseBeanService jobResponseService) {
        lazyModel = new JobLazyDataModel(jobResponseService);
    }
    
    @PostConstruct
    public void init() {
        logger.debug("** PostConstruct **");
        logger.debug(this.toString());
    }
        
    public LazyDataModel<JobResponseBean> getModel() {  
        return lazyModel;
    }
}
