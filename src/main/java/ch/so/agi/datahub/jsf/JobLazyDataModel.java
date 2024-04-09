package ch.so.agi.datahub.jsf;

import java.util.List;
import java.util.Map;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.slf4j.LoggerFactory;

import ch.so.agi.datahub.model.JobResponse;
import ch.so.agi.datahub.service.JobResponseService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobLazyDataModel extends LazyDataModel<JobResponse> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private JobResponseService jobResponseService;
    
    public JobLazyDataModel(JobResponseService jobResponseService) {
        this.jobResponseService = jobResponseService;
    }
    
    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        return jobResponseService.getJobResponseCount(filterBy);
    }

    @Override
    public List<JobResponse> load(int first, int pageSize, Map<String, SortMeta> sortBy,
            Map<String, FilterMeta> filterBy) {
        List<JobResponse> jobResponseList = jobResponseService.getJobResponseList(filterBy);
        int dataSize = jobResponseList.size();
        this.setRowCount(dataSize);
        return jobResponseList;        
    }
}
