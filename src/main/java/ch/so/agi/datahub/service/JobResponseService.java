package ch.so.agi.datahub.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SQLSelect;
import org.jobrunr.jobs.Job;
import org.jobrunr.storage.StorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ch.so.agi.datahub.AppConstants;
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
    
    public List<JobResponse> getJobsByOrg(Authentication authentication) {
        String organisation = null;
        if(authentication.getAuthorities().contains(new SimpleGrantedAuthority(AppConstants.ROLE_NAME_ADMIN))) {
            organisation = "%";
        } else {
            organisation = authentication.getName();
        }
        
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
    j.createdat AS createdat, 
    j.updatedat AS updatedat,
    j.state AS status, 
    queue_position.queueposition AS queueposition,
    op.aname AS operat,
    th.aname AS theme,
    org.aname AS organisation,
    CASE 
        WHEN isvalid IS TRUE THEN 'SUCCEEDED' 
        ELSE 'FAILED'
    END AS validationstatus,
    '$log_file_location' || d.jobid AS logfilelocation,
    'xtflogfilelocation'::TEXT AS xtflogfilelocation,
    'csvlogfilelocation'::TEXT AS csvlogfilelocation
FROM 
    $jobrunr_jobs_table AS j
    LEFT JOIN $delivery_table AS d 
    ON j.id = d.jobid 
    LEFT JOIN $apikey_table AS k 
    ON d.apikey_r = k.t_id     
    LEFT JOIN $organisation_table AS org 
    ON k.organisation_r = org.t_id 
    LEFT JOIN $operat_table AS op
    ON d.operat_r = op.t_id
    LEFT JOIN $theme_table AS th 
    ON op.theme_r = th.t_id    
    LEFT JOIN queue_position 
    ON queue_position.id = j.id
WHERE 
    org.aname LIKE '$organisation'
ORDER BY
    j.createdat DESC
                """;

        List<DataRow> results = SQLSelect
                .dataRowQuery(stmt)
                .param("log_file_location", getHost() + "/api/v1/logs/")
                .param("jobrunr_jobs_table", jobrunrDbSchema+".jobrunr_jobs")
                .param("delivery_table", dbSchema+".deliveries_delivery")
                .param("apikey_table", dbSchema+".core_apikey")
                .param("organisation_table", dbSchema+".core_organisation")
                .param("operat_table", dbSchema+".core_operat")
                .param("theme_table", dbSchema+".core_theme")
                .param("organisation", organisation)
                .select(objectContext);

        logger.debug("DataRow: {}", results);
                
        List<JobResponse> jobResponseList = results.stream().map(dr -> {
            JobResponse jobResponse = new JobResponse(
                    dr.get("createdat")!=null?((Date)dr.get("createdat")).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime():null,
                    dr.get("updatedat")!=null?((Date)dr.get("updatedat")).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime():null,
                    (String)dr.get("status"),
                    (Integer)dr.get("queueposition"),
                    (String)dr.get("operat"),
                    (String)dr.get("theme"),
                    (String)dr.get("organisation"),
                    (String)dr.get("message"),
                    (String)dr.get("validationstatus"), 
                    (String)dr.get("logfilelocation"), 
                    null, 
                    null 
                    );
            return jobResponse;
        }).collect(Collectors.toList());
        
        return jobResponseList;
    }

    public JobResponse getJobResponseById(String jobId, Authentication authentication) {            
        String organisation = null;
        if(authentication.getAuthorities().contains(new SimpleGrantedAuthority(AppConstants.ROLE_NAME_ADMIN))) {
            organisation = "%";
        } else {
            organisation = authentication.getName();
        }

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
    j.createdat AS createdat, 
    j.updatedat::TEXT AS updatedat,
    j.state AS status, 
    queue_position.queueposition AS queueposition,
    op.aname AS operat,
    th.aname AS theme,
    org.aname AS organisation,
    CASE 
        WHEN isvalid IS TRUE THEN 'SUCCEEDED' 
        ELSE 'FAILED'
    END AS validationstatus,
    '$log_file_location' || d.jobid AS logfilelocation,
    'xtflogfilelocation'::TEXT AS xtflogfilelocation,
    'csvlogfilelocation'::TEXT AS csvlogfilelocation
FROM 
    $jobrunr_jobs_table AS j
    LEFT JOIN $delivery_table AS d 
    ON j.id = d.jobid 
    LEFT JOIN $apikey_table AS k 
    ON d.apikey_r = k.t_id     
    LEFT JOIN $organisation_table AS org 
    ON k.organisation_r = org.t_id 
    LEFT JOIN $operat_table AS op
    ON d.operat_r = op.t_id
    LEFT JOIN $theme_table AS th 
    ON op.theme_r = th.t_id    
    LEFT JOIN queue_position 
    ON queue_position.id = j.id
    
WHERE 
    org.aname LIKE '$organisation'
AND 
    j.id = '$job_id'
                """;
        
        DataRow result = SQLSelect
                .dataRowQuery(stmt)
                .param("job_id", jobId)
                .param("log_file_location", getHost() + "/api/v1/logs/" + jobId)
                .param("jobrunr_jobs_table", jobrunrDbSchema+".jobrunr_jobs")
                .param("delivery_table", dbSchema+".deliveries_delivery")
                .param("apikey_table", dbSchema+".core_apikey")
                .param("organisation_table", dbSchema+".core_organisation")
                .param("operat_table", dbSchema+".core_operat")
                .param("theme_table", dbSchema+".core_theme")
                .param("organisation", organisation)
                .selectOne(objectContext);
        
        if (result == null) {
            return null;
        }
        
        JobResponse jobResponse = new JobResponse(
                result.get("createdat")!=null?((Date)result.get("createdat")).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime():null,
                result.get("updatedat")!=null?((Date)result.get("updatedat")).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime():null,
                (String)result.get("status"),
                (Integer)result.get("queueposition"),
                (String)result.get("operat"),
                (String)result.get("theme"),
                (String)result.get("organisation"),
                (String)result.get("message"),
                (String)result.get("validationstatus"), 
                (String)result.get("logfilelocation"), 
                null, //(String)result.get("xtflogfilelocation"), 
                null //(String)result.get("csvlogfilelocation")
                );
        
        return jobResponse;
    }
    
    private String getHost() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }
}
