package ch.so.agi.datahub;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityBeans {
    //@Value("${my.security.username}")
    private String username = "user";
    //@Value("${my.security.password}")
    private String password = "password";
    //@Value("${my.security.userRole}")
    private String userRole = "USER";

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService() {
//        UserDetails userDetails = User
//                .withUsername(username)
//                .password(passwordEncoder().encode(password))
//                .roles(userRole)
//                .build();
//
//        return new InMemoryUserDetailsManager(userDetails);
        return new MyUserDetailsService();
    }
}
