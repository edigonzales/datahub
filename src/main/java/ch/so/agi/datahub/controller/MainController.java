package ch.so.agi.datahub.controller;

import java.util.List;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SQLSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.so.agi.datahub.cayenne.CoreTheme;

@RestController
public class MainController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JdbcTemplate jdbcTemplate;
      
    @Value("${app.dbSchema}")
    private String dbSchema;

    @Autowired
    ObjectContext objectContext;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        logger.info("ping");        
        return new ResponseEntity<String>("datahub", HttpStatus.OK);
    }
    
    @GetMapping("/protected/hello")
    public String foo(Authentication authentication) {
        System.out.println("in controller: " + authentication.getCredentials());
        System.out.println("in controller: " + authentication.getName());
        return "Hello, this is a secured endpoint!";
    }
    
    @GetMapping("/public/hello")
    public String hello() {
        return "Hello, this is a _un_secured endpoint!";
    }
    
//    @PostMapping(value="/api/jobs", consumes = {"multipart/form-data"})
//    // @RequestPart anstelle von @RequestParam und @RequestBody damit swagger korrekt funktioniert.
//    // Sonst kann man zwar Dateien auswählen aber Swagger reklamiert im Browser, dass es Strings sein müssen.
//    public ResponseEntity<?> uploadFiles(@RequestPart(name="files", required=true) MultipartFile[] files/*, @RequestPart(name="theme", required=false) String theme*/) {
//
//        return ResponseEntity
//                .accepted()
//                //.header("Operation-Location", getHost()+"/api/jobs/"+jobId)
//                .body("Hallo Welt.");
//    }
}
