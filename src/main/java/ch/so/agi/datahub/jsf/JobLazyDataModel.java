package ch.so.agi.datahub.jsf;

import java.util.List;
import java.util.Map;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.slf4j.LoggerFactory;

import ch.so.agi.datahub.model.JobResponseBean;
import ch.so.agi.datahub.service.JobResponseBeanService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobLazyDataModel extends LazyDataModel<JobResponseBean> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private JobResponseBeanService jobResponseService;
    
    public JobLazyDataModel(JobResponseBeanService jobResponseService) {
        this.jobResponseService = jobResponseService;
    }
    
    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        return jobResponseService.getJobResponseCount(filterBy);
    }

    @Override
    public List<JobResponseBean> load(int first, int pageSize, Map<String, SortMeta> sortBy,
            Map<String, FilterMeta> filterBy) {
        logger.debug("load load load filters: " + filterBy.toString());
        List<JobResponseBean> jobResponseList = jobResponseService.getJobResponseList(filterBy);
        int dataSize = jobResponseList.size();
        this.setRowCount(dataSize);
        return jobResponseList;        
    }
}
