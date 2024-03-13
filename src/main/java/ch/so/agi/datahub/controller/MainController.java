package ch.so.agi.datahub.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        logger.info("ping");        
        return new ResponseEntity<String>("datahub", HttpStatus.OK);
    }
    
    @GetMapping("/protected/hello")
    public String foo(Authentication authentication) {
        System.out.println("Authorities: " + authentication.getAuthorities());
        System.out.println("Name: " + authentication.getName());
        return "Hello, this is a secured endpoint!";
    }
    
    @GetMapping("/public/hello")
    public String hello() {
        return "Hello, this is a _un_secured endpoint!";
    }
}
