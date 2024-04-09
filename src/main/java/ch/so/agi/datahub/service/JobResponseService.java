package ch.so.agi.datahub.service;

import java.util.Date;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SQLSelect;
import org.apache.cayenne.query.SortOrder;
import org.primefaces.model.FilterMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ch.so.agi.datahub.cayenne.VJobresponse;
import ch.so.agi.datahub.model.JobResponse;

@Service
public class JobResponseService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.responseListLimit}")
    private int responseListLimit;

    private ObjectContext objectContext;
    
    public JobResponseService (ObjectContext objectContext) {
        this.objectContext = objectContext;
    }
        
    public int getJobResponseCount(Map<String, FilterMeta> filterBy) {         
        ObjectSelect<DataRow> query = ObjectSelect.dataRowQuery(VJobresponse.class);
        
        for (Map.Entry<String, FilterMeta> entry : filterBy.entrySet()) {
            query.where(entry.getKey().toLowerCase() + " like $"+entry.getKey().toLowerCase(), entry.getValue().getFilterValue());
        }
        
        return (int) query.selectCount(objectContext);
    }
    
    public List<JobResponse> getJobResponseList() {
        return getJobResponseList(new HashMap<>());
    }
    
    public List<JobResponse> getJobResponseList(Map<String, FilterMeta> filterBy) {
        ObjectSelect<DataRow> query = ObjectSelect.dataRowQuery(VJobresponse.class);
        
        for (Map.Entry<String, FilterMeta> entry : filterBy.entrySet()) {
            // Achtung: Es wird immer eine like-Query gemacht. Dies funktioniert nicht mit boolean.
            query.where(entry.getKey().toLowerCase() + " like $"+entry.getKey().toLowerCase(), entry.getValue().getFilterValue()+"%");
        }
        query.orderBy(VJobresponse.CREATEDAT.getName(), SortOrder.DESCENDING).limit(responseListLimit);

        List<DataRow> rows = query.select(objectContext);
        logger.trace("DataRow: {}", rows);
                
        List<JobResponse> jobResponseList = rows.stream().map(dr -> {
            JobResponse jobResponse = new JobResponse(
                    (String)dr.get("jobid"),
                    (LocalDateTime)dr.get("createdat"),
                    (LocalDateTime)dr.get("updatedat"),
                    (String)dr.get("status"),
                    (Long)dr.get("queueposition"),
                    (String)dr.get("operat"),
                    (String)dr.get("theme"),
                    (String)dr.get("organisation"),
                    (String)dr.get("message"),
                    (String)dr.get("validationstatus"), 
                    dr.get("validationstatus")!=null?(getHost() + "/api/logs/" + (String)dr.get("jobid")):null, 
                    null, 
                    null 
                    );
            return jobResponse;
        }).collect(Collectors.toList());
        
        return jobResponseList;
    }

    public JobResponse getJobResponseById(String jobId) {        
        DataRow result = ObjectSelect.dataRowQuery(VJobresponse.class)
                .where("jobid = $jobid", jobId)
                .selectOne(objectContext);        
               
        if (result == null) {
            return null;
        }
        
        JobResponse jobResponse = new JobResponse(
                (String)result.get("jobid"),
                (LocalDateTime)result.get("createdat"),
                (LocalDateTime)result.get("updatedat"),
                (String)result.get("status"),
                (Long)result.get("queueposition"),
                (String)result.get("operat"),
                (String)result.get("theme"),
                (String)result.get("organisation"),
                (String)result.get("message"),
                (String)result.get("validationstatus"),
                result.get("validationstatus")!=null?(getHost() + "/api/logs/" + jobId):null, 
                null,
                null
                );
        
        return jobResponse;
    }
    
    private String getHost() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }    
}