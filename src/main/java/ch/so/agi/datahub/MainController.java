package ch.so.agi.datahub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/ping")
    public ResponseEntity<String>  ping() {
        logger.info("ping");
        return new ResponseEntity<String>("datahub", HttpStatus.OK);
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello, this is a secured endpoint!";
    }
    
    @GetMapping("/foo")
    public String foo() {
        return "Hello, this is a secured endpoint!";
    }
}
