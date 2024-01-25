package ch.so.agi.datahub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
//    @Bean
//    UserDetailsService userDetailsService() {
//        return new InMemoryUserDetailsManager(
//                User.withUsername("user")
//                    .password("password")
//                    .roles("USER")
//                    .build()
//        );
//    }
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
//    }
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
    
    private BasicAuthManager basicAuthManager;

    public SecurityConfig(BasicAuthManager basicAuthManager) {
        this.basicAuthManager = basicAuthManager;
    }
    
//    @Bean
//    UserDetailsService userDetailsService() {
//        UserDetails userDetails = User.withDefaultPasswordEncoder()
//            .username("user")
//            .password("password")
//            .roles("USER")
//            .build();
//        return new InMemoryUserDetailsManager(userDetails);
//    }
    
    // So geht das nicht, da man im CustomAuthorizationManager bereits authenficated ist.
    // Ich brauch sowas wie einen prefilter, der basic auth ausliest und prüft.
    
    // https://stackoverflow.com/questions/75683265/api-basic-authentication

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/ping").permitAll()
                //.requestMatchers("/api/jobs/**").access(new CustomAuthorizationManager())
                //.requestMatchers("/hello").access(new CustomAuthorizationManager())
                .requestMatchers("/**").authenticated() //.hasAnyRole
            )
            .authenticationManager(basicAuthManager)
            .httpBasic(Customizer.withDefaults());
            //.formLogin(Customizer.withDefaults());
        return http.build();
    }
}
