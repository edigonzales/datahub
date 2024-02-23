package ch.so.agi.datahub;

import java.io.File;

import javax.sql.DataSource;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import jakarta.annotation.PreDestroy;

@SpringBootApplication
public class DatahubApplication {

    @Autowired 
    private DataSource dataSource;
    
    private ServerRuntime cayenneRuntime;

    public static void main(String[] args) {
        SpringApplication.run(DatahubApplication.class, args);
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
}
