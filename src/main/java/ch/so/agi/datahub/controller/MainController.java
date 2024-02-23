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
    public ResponseEntity<String>  ping() {
        logger.info("ping");
        
//        List<CoreTheme> foo = ObjectSelect.query(CoreTheme.class).select(objectContext);
//        System.out.println(foo.get(0));

        String stmt = """
SELECT 
    t.themeid,
    t.config,
    t.metaconfig,
    op.operatid,
    op.t_id AS operatpk,
    u.t_id AS userpk
FROM 
    agi_datahub_v1.core_operat AS op 
    LEFT JOIN %s.core_organisation AS o 
    ON o.t_id = op.organisation_r 
    LEFT JOIN %s.core_organisation_user AS ou 
    ON o.t_id = ou.organisation_r 
    LEFT JOIN %s.core_user AS u 
    ON u.t_id = ou.user_r 
    LEFT JOIN %s.core_theme AS t 
    ON op.theme_r = t.t_id 
WHERE 
    u.userid = '$userid'
    AND 
    op.operatid = '$operatid'
    AND 
    t.themeid = '$themeid'
                """.formatted(dbSchema, dbSchema, dbSchema, dbSchema);

        DataRow result = SQLSelect
                .dataRowQuery(stmt)
                .param("userid", "bobXX")
                .param("operatid", "2549")
                .param("themeid", "NPLNF")
                .selectOne(objectContext);
                //.select(objectContext);

        System.out.println(result);
        
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
