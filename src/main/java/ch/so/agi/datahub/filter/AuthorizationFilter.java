package ch.so.agi.datahub.filter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SQLSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ch.so.agi.datahub.AppConstants;
import ch.so.agi.datahub.model.OperatDeliveryInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthorizationFilter extends OncePerRequestFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.dbSchema}")
    private String dbSchema;
    
    private ObjectContext objectContext;
    
    public AuthorizationFilter(ObjectContext objectContext) {
        this.objectContext = objectContext;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        logger.info("********* do authorization here...");
        
        HttpServletRequest servletRequest = (HttpServletRequest) request;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // TODO: kann das HIER null sein?
        
        String themeId = servletRequest.getParameter("theme");
        String operatId = servletRequest.getParameter("operat");
        
//        String stmt1 = """
//SELECT 
//    t.themeid,
//    t.config,
//    t.metaconfig,
//    op.operatid,
//    op.t_id AS operatpk,
//    u.t_id AS userpk
//FROM 
//    agi_datahub_v1.core_operat AS op 
//    LEFT JOIN %s.core_organisation AS o 
//    ON o.t_id = op.organisation_r 
//    LEFT JOIN %s.core_organisation_user AS ou 
//    ON o.t_id = ou.organisation_r 
//    LEFT JOIN %s.core_user AS u 
//    ON u.t_id = ou.user_r 
//    LEFT JOIN %s.core_theme AS t 
//    ON op.theme_r = t.t_id 
//WHERE 
//    u.userid = :userid
//    AND 
//    op.operatid = :operatid
//    AND 
//    t.themeid = :themeid
//                """.formatted(dbSchema, dbSchema, dbSchema, dbSchema);
//
//        Optional<OperatDeliveryInfo> operatDeliveryInfoOptional = jdbcClient.sql(stmt1)
//                .param("userid", userId)
//                //.param("userid", "gaga")
//                .param("themeid", themeId)
//                .param("operatid", operatId)
//                .query(OperatDeliveryInfo.class).optional();        
                
        String stmt = """
SELECT 
    t.themeid,
    t.config,
    t.metaconfig,
    op.operatid,
    op.t_id AS operatpk,
    u.t_id AS userpk
FROM 
    agi_datahub_v1.core_operat AS op 
    LEFT JOIN %s.core_organisation AS o 
    ON o.t_id = op.organisation_r 
    LEFT JOIN %s.core_organisation_user AS ou 
    ON o.t_id = ou.organisation_r 
    LEFT JOIN %s.core_user AS u 
    ON u.t_id = ou.user_r 
    LEFT JOIN %s.core_theme AS t 
    ON op.theme_r = t.t_id 
WHERE 
    u.userid = '$userid'
    AND 
    op.operatid = '$operatid'
    AND 
    t.themeid = '$themeid'
                """.formatted(dbSchema, dbSchema, dbSchema, dbSchema);

        DataRow result = SQLSelect
                .dataRowQuery(stmt)
                .param("userid", userId)
                .param("operatid", operatId)
                .param("themeid", themeId)
                .selectOne(objectContext);
        
        //if (operatDeliveryInfoOptional.isEmpty()) {        
        if (result == null) {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, "User is not authorized.");
        } else {
            //request.setAttribute(AppConstants.ATTRIBUTE_OPERAT_DELIVERY_INFO, operatDeliveryInfoOptional.get());
            request.setAttribute(AppConstants.ATTRIBUTE_OPERAT_DELIVERY_INFO, result);
            filterChain.doFilter(request, response);            
        }
    }
}
