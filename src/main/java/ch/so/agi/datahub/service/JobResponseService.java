package ch.so.agi.datahub.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SQLSelect;
import org.jobrunr.jobs.Job;
import org.jobrunr.storage.StorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ch.so.agi.datahub.model.JobResponse;
import ch.so.agi.datahub.model.JobResponse;

@Service
public class JobResponseService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.dbSchema}")
    private String dbSchema;
    
    // Jobrunr verlangt in den application.properties den Punkt für Schema-Support.
    @Value("#{'${org.jobrunr.database.tablePrefix}'.substring(0,'${org.jobrunr.database.tablePrefix}'.length-2)}")
    private String jobrunrDbSchema;

    private StorageProvider storageProvider;

    private ObjectContext objectContext;

    public JobResponseService (StorageProvider storageProvider, ObjectContext objectContext) {
        this.storageProvider = storageProvider;
        this.objectContext = objectContext;
    }
    
    public JobResponse getJobResponseById(String jobId) {
        //Job job = storageProvider.getJobById(UUID.fromString(jobId));        
        
        String stmt = """
WITH queue_position AS 
(
    SELECT 
        j.id, 
        createdat,
        ROW_NUMBER() OVER (ORDER BY createdat) AS queueposition
    FROM 
        agi_datahub_jobrunr_v1.jobrunr_jobs AS j
    WHERE 
        j.state = 'ENQUEUED'
)
SELECT 
    j.createdat::TEXT AS createdat, 
    j.updatedat::TEXT AS updatedat,
    j.state AS status, 
    queue_position.queueposition AS queueposition,
    NULL::TEXT AS message,
    CASE 
        WHEN isvalid IS TRUE THEN 'SUCCEEDED' 
        ELSE 'FAILED'
    END AS validationstatus,
    'logfilelocation'::TEXT AS logfilelocation,
    'xtflogfilelocation'::TEXT AS xtflogfilelocation,
    'csvlogfilelocation'::TEXT AS csvlogfilelocation
FROM 
    %s.jobrunr_jobs AS j
    LEFT JOIN %s.deliveries_delivery AS d 
    ON j.id = d.jobid 
    LEFT JOIN queue_position 
    ON queue_position.id = j.id
WHERE 
    j.id = '$jobid'
ORDER BY
    j.createdat ASC
                """.formatted(jobrunrDbSchema, dbSchema);
        DataRow result = SQLSelect
                .dataRowQuery(stmt)
                .param("jobid", jobId)
                .selectOne(objectContext);
        
        JobResponse jobResponse = new JobResponse(
                (String)result.get("createdat"),
                (String)result.get("updatedat"),
                (String)result.get("status"),
                (Integer)result.get("queueposition"),
                (String)result.get("message"),
                (String)result.get("validationstatus"), 
                (String)result.get("logfilelocation"), 
                (String)result.get("xtflogfilelocation"), 
                (String)result.get("csvlogfilelocation")
                );
        
        return jobResponse;
    }
}
