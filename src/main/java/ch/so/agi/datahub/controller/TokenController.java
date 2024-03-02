package ch.so.agi.datahub.controller;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import ch.so.agi.datahub.AppConstants;
import ch.so.agi.datahub.cayenne.CoreApikey;
import ch.so.agi.datahub.cayenne.CoreOrganisation;
import ch.so.agi.datahub.model.GenericResponse;
import ch.so.agi.datahub.model.TokenResponse;

@RestController
public class TokenController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.apiKeyHeaderName}")
    private String apiKeyHeaderName;

    private ObjectContext objectContext;
    
    private PasswordEncoder encoder;
    
    public TokenController(ObjectContext objectContext, PasswordEncoder encoder) {
        this.objectContext = objectContext;
        this.encoder = encoder;
    }
    
    @PostMapping(path = "/api/v1/token")
    public ResponseEntity<?> createToken(Authentication authentication, @RequestPart(name = "organisation", required = false) String organisationParam) {
        // Organisation eruieren, für die der neue API-Key erzeugt werden soll.
        
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
                    .body(new GenericResponse(this.getClass().getCanonicalName(), "organisation parameter is required", Instant.now()));
                    
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

        return ResponseEntity
                .ok(new TokenResponse(organisation, apiKey));
    }
    
    @DeleteMapping(path = "/api/v1/token/{apiKey}") 
    public ResponseEntity<?> deleteToken(Authentication authentication, @PathVariable(name = "apiKey") String apiKeyParam) {        
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
