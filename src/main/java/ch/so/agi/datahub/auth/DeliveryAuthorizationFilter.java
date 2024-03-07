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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.so.agi.datahub.AppConstants;
import ch.so.agi.datahub.model.GenericResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


// Wenn hier Exceptions auftreten, wird es weitergereicht und es folgt
// ein 403er. 
@Component
public class DeliveryAuthorizationFilter extends OncePerRequestFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.dbSchema}")
    private String dbSchema;
        
    private ObjectContext objectContext;
     
    private ObjectMapper mapper;
    
    public DeliveryAuthorizationFilter(ObjectContext objectContext, PasswordEncoder encoder, ObjectMapper mapper) {
        this.objectContext = objectContext;
        this.mapper = mapper;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        logger.debug("********* DO DELIVER AUTHORIZATION HERE...");
        
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String themeName = servletRequest.getParameter("theme");
        String operatName = servletRequest.getParameter("operat");
        
        if (themeName == null || operatName == null) {
            logger.error("theme or operat parameter is missing");
            
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ServletOutputStream responseStream = response.getOutputStream();
            mapper.writeValue(responseStream, new GenericResponse(this.getClass().getCanonicalName(), "Missing parameter", Instant.now()));
            responseStream.flush();
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String orgName = authentication.getName();
                 
        String stmt = """
SELECT 
    th.t_id AS theme_tid,
    th.aname AS theme_name, 
    th.config,
    th.metaconfig,
    op.t_id AS operat_tid,
    op.aname AS operat_name
FROM 
    %s.core_organisation AS org 
    LEFT JOIN %s.core_operat AS op 
    ON org.t_id = op.organisation_r 
    LEFT JOIN %s.core_theme AS th 
    ON th.t_id = op.theme_r 
WHERE 
    org.aname = '$orgname'
    AND 
    op.aname = '$opname'
    AND 
    th.aname = '$thname'
                """.formatted(dbSchema, dbSchema, dbSchema, dbSchema);
        
        DataRow result = SQLSelect
                .dataRowQuery(stmt)
                .param("orgname", orgName)
                .param("opname", operatName)
                .param("thname", themeName)
                .selectOne(objectContext);

        logger.debug("DataRow: {}", result);
        
        if (result == null) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ServletOutputStream responseStream = response.getOutputStream();
            mapper.writeValue(responseStream, new GenericResponse(this.getClass().getCanonicalName(), "User is not authorized", Instant.now()));
            responseStream.flush();
        } else {
            request.setAttribute(AppConstants.ATTRIBUTE_OPERAT_DELIVERY_INFO, result);
            filterChain.doFilter(request, response);            
        }
    }
}
