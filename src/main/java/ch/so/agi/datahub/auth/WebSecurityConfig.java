package ch.so.agi.datahub.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class WebSecurityConfig {
    
    @Autowired
    private UnauthorizedEntryPoint authenticationEntryPoint;
        
    //private ApiKeyAuthFilter apiKeyAuthFilter;
    
    // Bean-Methode darf nicht den gleichen Namen wie die Klasse haben.
//    @Bean
//    FilterRegistrationBean<AuthorizationFilter> authFilter(AuthorizationFilter authorizationFilter) {
//        FilterRegistrationBean<AuthorizationFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(authorizationFilter);
//        //registrationBean.addUrlPatterns("/api/v1/deliveries/**", "/und_noch_andere/*");
//        //registrationBean.addUrlPatterns("*");
//        registrationBean.addUrlPatterns("/api/v1/deliveries/*");
//        return registrationBean;
//    }
    
    @Bean
    ApiKeyAuthFilter authenticationFilter() {
        return new ApiKeyAuthFilter(); 
    }
    
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .securityMatcher("/**")
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/public/**")).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling ->
                    exceptionHandling.authenticationEntryPoint(authenticationEntryPoint))
                // message body is missng: https://www.baeldung.com/spring-security-basic-authentication??
                .build();
    }

    
    
//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
////        http.authorizeHttpRequests((authorize) -> authorize
////                .requestMatchers("/ping").permitAll()
////                );
//
//        http
//        .securityMatcher("/api/jobs/**", "/api/v1/deliveries/**") // Sonst wird ein allfälliger Filter hier (z.B. addFilterAfter()) auf alle Requests angewendet.
//        .csrf(csrf -> csrf.disable()) // Für non-browser i.O. (?)
//        .authorizeHttpRequests((authorize) -> authorize
//                //.requestMatchers("/ping").permitAll()
//                //.requestMatchers("/api/jobs/**").authenticated()
//                .requestMatchers("/api/v1/deliveries/**").authenticated()
//        )
//        //TODO Authorisierung eher nicht hier. Wenn man z.B. mit GUI sich anmeldet, schickt
//        // man noch keine Datei. Gut, da wäre URL anders. Aber eben, gibt viele
//        // Varianten.
//        //.addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
//        .httpBasic(Customizer.withDefaults());
//        //.formLogin(Customizer.withDefaults());
//
//      return http.build();
//    }
    
//    @Autowired
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//        .ldapAuthentication()
//            .userDnPatterns("uid={0},ou=people") // Specifies the patterns used to search for user Distinguished Names (DNs) during authentication.
//            .groupSearchBase("ou=people") // Specifies the base DN (Distinguished Name) under which the search for LDAP groups will be performed.
//            .contextSource()
//                //.ldif("classpath:test-server.ldif")
//                .root("dc=springframework,dc=org");
//    }
}
