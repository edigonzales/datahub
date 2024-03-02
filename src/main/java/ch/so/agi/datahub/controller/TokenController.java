package ch.so.agi.datahub.controller;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

import ch.so.agi.datahub.cayenne.CoreApikey;
import ch.so.agi.datahub.cayenne.CoreOrganisation;
import ch.so.agi.datahub.model.ErrorResponse;
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
    public ResponseEntity<?> addToken(Authentication authentication, @RequestPart(name = "organisation", required = true) String organisation) {
        // Falls Admin-Organisation requestet, wird in Tokenauthorisierung nicht implizit geprüft,
        // ob Organisation vorhanden ist.
        CoreOrganisation coreOrganisation = ObjectSelect.query(CoreOrganisation.class)
                .where(CoreOrganisation.ANAME.eq(organisation))
                .selectFirst(objectContext);
        
        if (coreOrganisation == null) {
            logger.error("Organisation '{}' not found.", organisation);
            return ResponseEntity
                    .internalServerError()
                    .body(new ErrorResponse(this.getClass().getCanonicalName(), "Object not found.", Instant.now()));
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
    
//    // Es wird nicht geprüft, ob 
//    @DeleteMapping(path = "/api/v1/token/{apiKey}") 
//    public ResponseEntity<?> deleteToken(@PathVariable String apiKey) {
//        
//    }

}
