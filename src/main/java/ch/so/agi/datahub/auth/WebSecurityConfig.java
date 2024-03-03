package ch.so.agi.datahub.auth;

import org.apache.cayenne.ObjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class WebSecurityConfig {

    @Value("${app.apiKeyHeaderName}")
    private String apiKeyHeaderName;
    
    @Autowired
    @Qualifier("customAuthenticationEntryPoint")
    AuthenticationEntryPoint authEntryPoint;

    @Autowired
    ObjectContext objectContext;
    
    @Autowired
    PasswordEncoder encoder;
    
    //private ApiKeyAuthFilter apiKeyAuthFilter;
    
    // Bean-Methode darf nicht den gleichen Namen wie die Klasse haben.
    @Bean
    FilterRegistrationBean<DeliveryAuthorizationFilter> deliveryAuthFilter(DeliveryAuthorizationFilter authorizationFilter) {
        FilterRegistrationBean<DeliveryAuthorizationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(authorizationFilter);
        //registrationBean.addUrlPatterns("/api/v1/deliveries/**", "/und_noch_andere/*");
        //registrationBean.addUrlPatterns("*");
        registrationBean.addUrlPatterns("/api/v1/deliveries/*");
        return registrationBean;
    }
    
    
    // Braucht es das überhaupt, wenn man nicht noch die Organisation als Parameter übergibt.
    // Dann gibt es m.E. nur die Authentifizierung und es wird für die Organisation
    // ein neuer Key erzeugt, die dem gültigen Key entspricht.
    // 
//    @Bean
//    FilterRegistrationBean<TokenAuthorizationFilter> tokenAuthFilter(TokenAuthorizationFilter tokenAuthorizationFilter) {
//        FilterRegistrationBean<TokenAuthorizationFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(tokenAuthorizationFilter);
//        registrationBean.addUrlPatterns("/api/v1/token/*");
//        return registrationBean;
//    }

    @Bean
    ApiKeyAuthenticationManager authenticationManager() {
        return new ApiKeyAuthenticationManager(objectContext, encoder);
    }
    
    @Bean
    ApiKeyAuthFilter authenticationFilter() {
        ApiKeyAuthFilter filter = new ApiKeyAuthFilter(apiKeyHeaderName);
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {                
        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .securityMatcher("/**")
                .addFilter(authenticationFilter())
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/public/**")).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling ->
                    exceptionHandling.authenticationEntryPoint(authEntryPoint)
                )
                .logout(AbstractHttpConfigurer::disable)
                .build();
    }
}
