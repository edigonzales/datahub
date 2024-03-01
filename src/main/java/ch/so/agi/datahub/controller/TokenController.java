package ch.so.agi.datahub.controller;

import java.io.IOException;

import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;

@Controller
public class TokenController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.apiKeyHeaderName}")
    private String apiKeyHeaderName;

    private ObjectContext objectContext;
    
    public TokenController(ObjectContext objectContext) {
        this.objectContext = objectContext;
    }
    
    @PostMapping(path = "/api/v1/token")
    public ResponseEntity<?> addToken(@RequestPart(name = "organisation", required = true) String organisation) throws IOException {

        
        return null;
    }
}
