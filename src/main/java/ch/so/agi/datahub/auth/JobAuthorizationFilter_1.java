package ch.so.agi.datahub.auth;

import java.io.IOException;
import java.time.Instant;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SQLSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.so.agi.datahub.AppConstants;
import ch.so.agi.datahub.model.GenericResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// DISABLE
//@Component
public class JobAuthorizationFilter_1 extends OncePerRequestFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final static String JOB_PATH_ELE = "jobs";

    @Value("${app.dbSchema}")
    private String dbSchema;
        
    private ObjectContext objectContext;

    private ObjectMapper mapper;
    
    public JobAuthorizationFilter_1(ObjectContext objectContext, ObjectMapper mapper) {
        this.objectContext = objectContext;
        this.mapper = mapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {        
        // Use cases:
        // - Ein spezifischer Job. Es wird Job-ID mitgeliefert als Path-Variable. Anhand dieser muss geprüft werden,
        // ob der Api-Key berechtigt ist.
        // - Alle Jobs für die der Api-Key berechtigt ist. Hier ist vorstellbar, dass mit Query-Parameter z.B. das
        // Thema gefiltert werden kann. Es muss/soll nicht geprüft werden, ob der Api-Key das Thema sehen darf. 
        // Dies ergibt sich aus der Query / dem Join Organisation <-> Thema.
        // -> Authorisierung nur für einzelne Job-ID.
        
        logger.debug("********* DO JOB AUTHORIZATION HERE...");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String orgName = authentication.getName();
        
        String requestURI = request.getRequestURI();
        String[] pathElements = requestURI.split("/");
        
        if (pathElements[pathElements.length-1].equalsIgnoreCase(JOB_PATH_ELE)) {
            logger.debug("request all jobs");
            // Authentifizierung reicht, da nur die Businesslogik nur die Jobs aus der DB
            // holt, für die der Key / die Organisation berechtigt ist.
            filterChain.doFilter(request, response);
        } else if (pathElements.length >= 2 && pathElements[pathElements.length-2].equalsIgnoreCase(JOB_PATH_ELE)) {
            logger.debug("request single job");
            String jobId = pathElements[pathElements.length-1];
            
            String stmt = """
SELECT 
    o.aname
FROM 
    %s.deliveries_delivery AS d 
    LEFT JOIN %s.core_apikey AS k 
    ON d.apikey_r = k.t_id 
    LEFT JOIN %s.core_organisation AS o 
    ON k.organisation_r = o.t_id 
WHERE 
    d.jobid = '$jobid'
AND 
    o.aname = '$orgname'
                    """.formatted(dbSchema, dbSchema, dbSchema);

            DataRow result = SQLSelect
                    .dataRowQuery(stmt)
                    .param("jobid", jobId)
                    .param("orgname", orgName)
                    .selectOne(objectContext);

            logger.debug("DataRow: {}", result);

            if (result == null) {
                sendErrorResponse(response);
            } else {
                filterChain.doFilter(request, response);            
            }
        } else {
            logger.error("incorrect api request: {}", requestURI);
            
            sendErrorResponse(response);
        }
    }

    private void sendErrorResponse(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        ServletOutputStream responseStream = response.getOutputStream();
        mapper.writeValue(responseStream, new GenericResponse(this.getClass().getCanonicalName(), "User is not authorized", Instant.now()));
        responseStream.flush();
    }
}
