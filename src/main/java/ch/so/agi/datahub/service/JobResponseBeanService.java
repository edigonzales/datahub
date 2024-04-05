package ch.so.agi.datahub.service;

import java.util.Date;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SQLSelect;
import org.primefaces.model.FilterMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ch.so.agi.datahub.model.JobResponseBean;

@Service
public class JobResponseBeanService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.dbSchemaConfig}")
    private String dbSchemaConfig;
    
    @Value("${app.dbSchemaLog}")
    private String dbSchemaLog;
    
    // Jobrunr verlangt in den application.properties den Punkt für Schema-Support.
    @Value("#{'${org.jobrunr.database.tablePrefix}'.substring(0,'${org.jobrunr.database.tablePrefix}'.length-2)}")
    private String dbSchemaJobrunr;

    private ObjectContext objectContext;
    
    public JobResponseBeanService (ObjectContext objectContext) {
        this.objectContext = objectContext;
    }
    
    private String getWhereClause(Map<String, FilterMeta> filterBy) {
        String whereClause = "";
        int i=0;
        for (Map.Entry<String, FilterMeta> entry : filterBy.entrySet()) {
            if (i==0) {
                whereClause += " AND ";
            } else {
                whereClause += " AND ";
            } 
            String attrName = entry.getKey();
//            logger.info("attrName: " + attrName);
            if (attrName.equalsIgnoreCase("validationStatus")) {
                attrName = "isvalid";
            }
                        
            whereClause += " " + attrName + getOperatorAndFilterValue((String)entry.getValue().getFilterValue()); 
            
            i++;
        }
        return whereClause;
    }
    
    private String getOperatorAndFilterValue(String rawFilterValue) {
        return switch(rawFilterValue) {
            case "true" -> " IS true";
            case "false" -> " IS false";                
            default -> " ILIKE '"+rawFilterValue+"%'";
        };
    }
    
    public int getJobResponseCount(Map<String, FilterMeta> filterBy) {
//        System.out.println("service: getJobResponseCount");
        
        String stmt = "SELECT CAST(count(*) AS INTEGER) AS cnt FROM " + dbSchemaLog + ".deliveries_delivery WHERE 1=1 " + getWhereClause(filterBy);
//        logger.debug(stmt);
        
        int count = (int) SQLSelect.dataRowQuery(stmt).selectOne(objectContext).get("cnt");
        return count;
    }
    
    public List<JobResponseBean> getJobResponseList(Map<String, FilterMeta> filterBy) {
//        String organisation = null;
//        if(authentication.getAuthorities().contains(new SimpleGrantedAuthority(AppConstants.ROLE_NAME_ADMIN))) {
//            organisation = "%";
//        } else {
//            organisation = authentication.getName();
//        }
        
//        System.out.println("service: getJobResponseList");

        String stmt = baseStmt + getWhereClause(filterBy) + " ORDER BY j.createdat DESC LIMIT 300";        
//        logger.debug(stmt);
        
        List<DataRow> results = SQLSelect
                .dataRowQuery(stmt)
                .param("log_file_location", getHost() + "/api/logs/")
                .param("jobrunr_jobs_table", dbSchemaJobrunr+".jobrunr_jobs")
                .param("delivery_table", dbSchemaLog+".deliveries_delivery")
                .param("apikey_table", dbSchemaConfig+".core_apikey")
                .param("organisation_table", dbSchemaConfig+".core_organisation")
                .param("operat_table", dbSchemaConfig+".core_operat")
                .param("theme_table", dbSchemaConfig+".core_theme")
                .param("organisation", "%")
                .select(objectContext);

        logger.trace("DataRow: {}", results);
                
        List<JobResponseBean> jobResponseList = results.stream().map(dr -> {
            JobResponseBean jobResponse = new JobResponseBean(
                    (String)dr.get("jobid"),
                    dr.get("createdat")!=null?((Date)dr.get("createdat")).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime():null,
                    dr.get("updatedat")!=null?((Date)dr.get("updatedat")).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime():null,
                    (String)dr.get("status"),
                    (Long)dr.get("queueposition"),
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

//    public JobResponse getJobResponseById(String jobId) {            
////        String organisation = null;
////        if(authentication.getAuthorities().contains(new SimpleGrantedAuthority(AppConstants.ROLE_NAME_ADMIN))) {
////            organisation = "%";
////        } else {
////            organisation = authentication.getName();
////        }
//        
//        // Ich verstehe nicht ganz, wie Jobrunr das Datum handelt.
//        // Es ist um eine Stunde falsch, aber in der DB hat es keine
//        // Timezone. Via jobrunr-API bekomme ich das richtige DateTime.
//        // Ob meine Lösung nun stimmt, wird sich zeigen. Timezone 
//        // könnte man noch parametrisieren.
//
//        String stmt = baseStmt + "AND j.id = '$job_id'";
//
//        DataRow result = SQLSelect
//                .dataRowQuery(stmt)
//                .param("job_id", jobId)
//                .param("log_file_location", getHost() + "/api/logs/" + jobId)
//                .param("jobrunr_jobs_table", dbSchemaJobrunr+".jobrunr_jobs")
//                .param("delivery_table", dbSchemaLog+".deliveries_delivery")
//                .param("apikey_table", dbSchemaConfig+".core_apikey")
//                .param("organisation_table", dbSchemaConfig+".core_organisation")
//                .param("operat_table", dbSchemaConfig+".core_operat")
//                .param("theme_table", dbSchemaConfig+".core_theme")
//                .param("organisation", "%")
//                .selectOne(objectContext);
//               
//        if (result == null) {
//            return null;
//        }
//        
//        JobResponse jobResponse = new JobResponse(
//                (String)result.get("jobid"),
//                result.get("createdat")!=null?((Date)result.get("createdat")).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime():null,
//                result.get("updatedat")!=null?((Date)result.get("updatedat")).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime():null,
//                (String)result.get("status"),
//                (Long)result.get("queueposition"),
//                (String)result.get("operat"),
//                (String)result.get("theme"),
//                (String)result.get("organisation"),
//                (String)result.get("message"),
//                (String)result.get("validationstatus"), 
//                (String)result.get("logfilelocation"), 
//                null, //(String)result.get("xtflogfilelocation"), 
//                null //(String)result.get("csvlogfilelocation")
//                );
//        
//        return jobResponse;
//    }
    
    private String getHost() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }
    
    private String baseStmt = """
WITH queue_position AS 
(
    SELECT 
        j.id, 
        createdat,
        ROW_NUMBER() OVER (ORDER BY createdat) AS queueposition
    FROM 
        $jobrunr_jobs_table AS j
    WHERE 
        j.state = 'ENQUEUED'
)
SELECT 
    d.jobid,
    (j.createdat AT TIME ZONE 'UTC') AT TIME ZONE 'Europe/Zurich' AS createdat,
    (j.updatedat AT TIME ZONE 'UTC') AT TIME ZONE 'Europe/Zurich' AS updatedat,
    CASE 
        WHEN state = 'SUCCEEDED' THEN 'SUCCEEDED'
        ELSE state
    END as status, 
    queue_position.queueposition AS queueposition,
    op.aname AS operat,
    th.aname AS theme,
    org.aname AS organisation,
    CASE 
        WHEN isvalid IS TRUE THEN 'DONE' 
        WHEN isvalid IS FALSE THEN 'FAILED'
        ELSE NULL::TEXT
    END AS validationstatus,
    CASE
        WHEN isvalid IS NOT NULL THEN '$log_file_location' || d.jobid
        ELSE NULL::TEXT
    END AS logfilelocation,
    'xtflogfilelocation'::TEXT AS xtflogfilelocation,
    'csvlogfilelocation'::TEXT AS csvlogfilelocation
FROM 
    $jobrunr_jobs_table AS j
    LEFT JOIN $delivery_table AS d 
    ON j.id = d.jobid 
    LEFT JOIN $organisation_table AS org 
    ON org.aname = d.organisation 
    LEFT JOIN $operat_table AS op
    ON d.operat = op.aname
    LEFT JOIN $theme_table AS th 
    ON op.theme_r = th.t_id    
    LEFT JOIN queue_position 
    ON queue_position.id = j.id
WHERE 
    org.aname IS NOT NULL
            """;
}
