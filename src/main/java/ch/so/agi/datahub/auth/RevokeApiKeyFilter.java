package ch.so.agi.datahub.auth;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.so.agi.datahub.cayenne.CoreApikey;
import ch.so.agi.datahub.cayenne.CoreOrganisation;
import ch.so.agi.datahub.model.GenericResponse;
import ch.so.agi.datahub.service.EmailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//@Component
public class RevokeApiKeyFilter extends OncePerRequestFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    ObjectContext objectContext;
    
    PasswordEncoder encoder;
    
    ObjectMapper mapper;
        
    EmailService emailService;
    
    final String headerName;
    
    ResourceBundle resourceBundle;
    
    public RevokeApiKeyFilter(final String headerName, ObjectContext objectContext, PasswordEncoder encoder,
            EmailService emailService, ObjectMapper mapper, ResourceBundle resourceBundle) {
        this.headerName = headerName;
        this.objectContext = objectContext;
        this.encoder = encoder;
        this.emailService = emailService;
        this.mapper = mapper;
        this.resourceBundle = resourceBundle;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String host = ServletUriComponentsBuilder.fromCurrentRequest().build().getHost();
        String proto = ServletUriComponentsBuilder.fromCurrentRequest().build().getScheme();
        String key = request.getHeader(headerName);

        if (key != null) {
            if (proto.equalsIgnoreCase("http") && !host.equalsIgnoreCase("localhost")) {                
                List<CoreApikey> apiKeys = ObjectSelect.query(CoreApikey.class)
                        .where(CoreApikey.REVOKEDAT.isNull())
                        .and(CoreApikey.DATEOFEXPIRY.gt(LocalDateTime.now()).orExp(CoreApikey.DATEOFEXPIRY.isNull()))
                        .select(objectContext);
                
                CoreApikey myApiKey = null;
                for (CoreApikey apiKey : apiKeys) {
                    if (encoder.matches(key, apiKey.getApikey())) {
                        myApiKey = apiKey;
                        break;
                    }
                }

                if (myApiKey != null) {
                    myApiKey.setRevokedat(LocalDateTime.now());
                    objectContext.commitChanges();                    
                    try {
                        CoreOrganisation coreOrganisation = myApiKey.getCoreOrganisation();
                        emailService.send(coreOrganisation.getEmail(), resourceBundle.getString("revokeApiKeyEmailSubject"), resourceBundle.getString("revokeApiKeyEmailBody"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error(e.getMessage());                        
                    }
                }

                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                ServletOutputStream responseStream = response.getOutputStream();
                mapper.writeValue(responseStream, new GenericResponse(this.getClass().getCanonicalName(),
                        "Possible API key was revoked.", Instant.now()));
                responseStream.flush();
                return;
            }            
        }

        filterChain.doFilter(request, response);
    }
}
