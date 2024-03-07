package ch.so.agi.datahub.auth;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.so.agi.datahub.AppConstants;
import ch.so.agi.datahub.cayenne.CoreApikey;
import ch.so.agi.datahub.cayenne.CoreOrganisation;
import ch.so.agi.datahub.model.GenericResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// DISABLE
//@Component
public class TokenAuthorizationFilter1 extends OncePerRequestFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.dbSchema}")
    private String dbSchema;
    
    @Autowired
    ObjectMapper mapper;

    private ObjectContext objectContext;
    
    private PasswordEncoder encoder;
    
    public TokenAuthorizationFilter1(ObjectContext objectContext, PasswordEncoder encoder) {
        this.objectContext = objectContext;
        this.encoder = encoder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        logger.debug("********* TOKEN AUTHORIZATION FILTER");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        logger.info(authentication.getAuthorities().toString());
        
        
        
        String organisationParam = request.getParameter("organisation");
        logger.info(organisationParam);
        
        // Entweder ist man ADMIN, dann darf man für alle Organisationen einen neuen Key erzeugen
        // oder man ist USER und darf nur für seine eigene Organisation einen neuen Key erzeugen.
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(AppConstants.ROLE_NAME_ADMIN))) {
            filterChain.doFilter(request, response);            
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(AppConstants.ROLE_NAME_USER))) {
            // TODO: exception handling, falls es mehrere findet.
            // Dürte aber mit funktionierendem (ili2pg?) unique nicht vorkommen.
            CoreOrganisation organisation = ObjectSelect.query(CoreOrganisation.class)
                    .where(CoreOrganisation.ANAME.eq(organisationParam))
                    .selectOne(objectContext);
            
            CoreApikey myApiKey = null;
            for (CoreApikey apiKey : organisation.getCoreApikeys()) {                
                if (encoder.matches((String) authentication.getPrincipal(), apiKey.getApikey())) {
                    // Theoretisch könnte es den gleichen Key auch in gültig / nicht revoked geben.
                    // Wahrscheinlichkeit ist aber seeeehr gering.
                    if (apiKey.getDateofexpiry() != null && apiKey.getDateofexpiry().isBefore(LocalDateTime.now())) {
                        break;
                    }
                    if (apiKey.getRevokedat() != null) {
                        break;
                    }
                    myApiKey = apiKey;
                    filterChain.doFilter(request, response);
                    break;
                }
            }
            
            if (myApiKey == null) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                ServletOutputStream responseStream = response.getOutputStream();
                mapper.writeValue(responseStream, new GenericResponse(this.getClass().getCanonicalName(), "User is not authorized", Instant.now()));
                responseStream.flush();
            }
        }
    }
}
