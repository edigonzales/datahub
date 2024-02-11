package ch.so.agi.datahub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class MainController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/ping")
    public ResponseEntity<String>  ping() {
        logger.info("ping");
        return new ResponseEntity<String>("datahub", HttpStatus.OK);
    }
    
    @GetMapping("/foo")
    public String foo() {
        return "Hello, this is a secured endpoint!";
    }
    
    @GetMapping("/hello")
    public String hello() {
        return "Hello, this is a secured endpoint!";
    }

    
    @PostMapping(value="/api/jobs", consumes = {"multipart/form-data"})
    // @RequestPart anstelle von @RequestParam und @RequestBody damit swagger korrekt funktioniert.
    // Sonst kann man zwar Dateien auswählen aber Swagger reklamiert im Browser, dass es Strings sein müssen.
    public ResponseEntity<?> uploadFiles(@RequestPart(name="files", required=true) MultipartFile[] files/*, @RequestPart(name="theme", required=false) String theme*/) {

        return ResponseEntity
                .accepted()
                //.header("Operation-Location", getHost()+"/api/jobs/"+jobId)
                .body("Hallo Welt.");
    }
}
