package ch.so.agi.datahub.filter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ch.so.agi.datahub.model.Operat;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthorizationFilter extends OncePerRequestFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private JdbcClient jdbcClient;
    
    public AuthorizationFilter(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
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
        
        String stmt = """
SELECT 
    t.themeid,
    t.config,
    t.metaconfig,
    op.operatid
FROM 
    agi_datahub_v1.core_operat AS op 
    LEFT JOIN agi_datahub_v1.core_organisation AS o 
    ON o.t_id = op.organisation_r 
    LEFT JOIN agi_datahub_v1.core_organisation_user AS ou 
    ON o.t_id = ou.organisation_r 
    LEFT JOIN agi_datahub_v1.core_user AS u 
    ON u.t_id = ou.user_r 
    LEFT JOIN agi_datahub_v1.core_theme AS t 
    ON op.theme_r = t.t_id 
WHERE 
    u.userid = :userid
    AND 
    op.operatid = :operatid
    AND 
    t.themeid = :themeid
                """;
        
        // TODO Eventuell OperatUser mit Infos über User (isActive, Role, ...)

        Optional<Operat> operatOptional = jdbcClient.sql(stmt)
        //Operat operat = jdbcClient.sql(stmt)
                .param("userid", userId)
                //.param("userid", "gaga")
                .param("themeid", themeId)
                .param("operatid", operatId)
                .query(Operat.class).optional();        
                
        if (operatOptional.isEmpty()) {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, "User is not authorized.");
        } else {
            filterChain.doFilter(request, response);            
        }
    }
}
