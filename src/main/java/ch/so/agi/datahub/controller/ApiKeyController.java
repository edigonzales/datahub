package ch.so.agi.datahub.controller;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import ch.so.agi.datahub.AppConstants;
import ch.so.agi.datahub.cayenne.CoreApikey;
import ch.so.agi.datahub.cayenne.CoreOrganisation;
import ch.so.agi.datahub.model.GenericResponse;

@RestController
public class ApiKeyController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.apiKeyHeaderName}")
    private String apiKeyHeaderName;

    @Value("${spring.mail.username}")
    private String mailUsername;

    private ObjectContext objectContext;
    
    private PasswordEncoder encoder;
    
    private JavaMailSender emailSender;
    
    public ApiKeyController(ObjectContext objectContext, PasswordEncoder encoder, JavaMailSender emailSender) {
        this.objectContext = objectContext;
        this.encoder = encoder;
        this.emailSender = emailSender;
    }
    
    @PostMapping(path = "/api/v1/key")
    public ResponseEntity<?> createApiKey(Authentication authentication, @RequestPart(name = "organisation", required = false) String organisationParam) {
        // Organisation eruieren, für die der neue API-Key erzeugt werden soll.
        // Falls es ein Admin-Key (resp. Org) ist, muss die Organisation als Parameter geliefert 
        // werden.
        // Falls die Organisation selber einen neuen Key braucht, kennt man zum Request-Key 
        // gehörende Organisation.
        String organisation = null;
        if(authentication.getAuthorities().contains(new SimpleGrantedAuthority(AppConstants.ROLE_NAME_ADMIN))) {
            organisation = organisationParam;
        } else {
            organisation = ((CoreApikey) authentication.getDetails()).getCoreOrganisation().getAname();
        }
        
        if (organisation == null) {
            logger.error("organisation parameter is required");
            return ResponseEntity
                    .internalServerError()
                    .body(new GenericResponse(this.getClass().getCanonicalName(), "Parameter 'organisation' is required.", Instant.now()));
                    
        }
        
        CoreOrganisation coreOrganisation = ObjectSelect.query(CoreOrganisation.class)
                .where(CoreOrganisation.ANAME.eq(organisation))
                .selectOne(objectContext);
    
        if (coreOrganisation == null) {
            logger.error("Organisation '{}' not found.", organisation);
            return ResponseEntity
                    .internalServerError()
                    .body(new GenericResponse(this.getClass().getCanonicalName(), "Object not found.", Instant.now()));
        }
        
        String apiKey = UUID.randomUUID().toString();
        String encodedApiKey = encoder.encode(apiKey);
        
        CoreApikey coreApiKey = objectContext.newObject(CoreApikey.class);
        coreApiKey.setApikey(encodedApiKey);
        coreApiKey.setCreatedat(LocalDateTime.now());
        coreApiKey.setCoreOrganisation(coreOrganisation);
        
        objectContext.commitChanges();

        try {
            SimpleMailMessage message = new SimpleMailMessage(); 
            message.setFrom(mailUsername);
            message.setTo(coreOrganisation.getEmail()); 
            message.setSubject("datahub: new api key"); 
            message.setText(apiKey);
            emailSender.send(message);            
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            
            return ResponseEntity
                    .internalServerError()
                    .body(new GenericResponse(this.getClass().getCanonicalName(), "Error while sending email.", Instant.now()));
        }

        return ResponseEntity
                .ok(new GenericResponse(null, "Sent email with new key.", Instant.now()));
    }
    
    @DeleteMapping(path = "/api/v1/key/{apiKey}") 
    public ResponseEntity<?> deleteApiKey(Authentication authentication, @PathVariable(name = "apiKey") String apiKeyParam) {        
        List<CoreApikey> apiKeys = ObjectSelect.query(CoreApikey.class)
                .where(CoreApikey.REVOKEDAT.isNull())
                .select(objectContext);
        
        for (CoreApikey apiKey : apiKeys) {
            if (encoder.matches(apiKeyParam, apiKey.getApikey())) {
                apiKey.setRevokedat(LocalDateTime.now());
                objectContext.commitChanges();
                
                return ResponseEntity
                        .ok().body(new GenericResponse(null, "Key deleted.", Instant.now()));
            }
        }

        return ResponseEntity
                .internalServerError()
                .body(new GenericResponse(this.getClass().getCanonicalName(), "Key not deleted.", Instant.now()));
    }

}
