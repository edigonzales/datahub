package ch.so.agi.datahub;

import java.io.File;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.ObjectSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ch.so.agi.datahub.cayenne.CoreApikey;
import ch.so.agi.datahub.cayenne.CoreOrganisation;
import ch.so.agi.datahub.cayenne.DeliveriesAsset;
import jakarta.annotation.PreDestroy;

@Configuration
@EnableScheduling
@SpringBootApplication
public class DatahubApplication {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.adminAccountInit}")
    private boolean adminAccountInit;

    @Value("${app.adminAccountName}")
    private String adminAccountName;

    @Autowired 
    private DataSource dataSource;
    
    private ServerRuntime cayenneRuntime;

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(DatahubApplication.class, args);
        
//        String[] allBeanNames = applicationContext.getBeanDefinitionNames();
//        for(String beanName : allBeanNames) {
//            System.out.println(beanName);
//        }
    }

    @Bean
    ObjectContext objectContext() {        
        ServerRuntime cayenneRuntime = ServerRuntime.builder()
                .dataSource(dataSource)
                .addConfig("cayenne/cayenne-project.xml")
                .build();

        ObjectContext context = cayenneRuntime.newContext();
        
        return context;
    }
    
    @PreDestroy
    public void shutdownCayenne() {
        cayenneRuntime.shutdown();
    }
        
    @Bean
    CommandLineRunner init() {
        return args -> {
            // Add admin account to database.
            // Show admin key once in the console.
            if (adminAccountInit) {
                ObjectContext objectContext = objectContext();

                CoreOrganisation existingOrg = ObjectSelect.query(CoreOrganisation.class)
                        .where(CoreOrganisation.ANAME.eq(adminAccountName))
                        .selectFirst(objectContext);

                if (existingOrg != null) {
                    logger.warn("Account name '{}' already exists.", adminAccountName);
                    return;
                }

                CoreOrganisation coreOrganisation = objectContext.newObject(CoreOrganisation.class);
                coreOrganisation.setAname(adminAccountName);
                coreOrganisation.setArole("ADMIN");
                coreOrganisation.setEmail("stefan.ziegler@bd.so.ch");
                
                String apiKey = UUID.randomUUID().toString();
                String encodedApiKey = encoder().encode(apiKey);
                
                CoreApikey coreApiKey = objectContext.newObject(CoreApikey.class);
                coreApiKey.setApikey(encodedApiKey);
                coreApiKey.setCreatedat(LocalDateTime.now());
                coreApiKey.setCoreOrganisation(coreOrganisation);
                
                objectContext.commitChanges();
                
                logger.warn("************************************************************");
                logger.warn(apiKey);
                logger.warn("************************************************************");
            }
        };
    }
    
    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
