package ch.so.agi.datahub.auth;

import org.apache.cayenne.ObjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

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
    
    // Braucht es m.E. nicht: Werden alle Jobs angefordert, werden nach der Authentifizierung nur
    // die Jobs der anfordernden Organisation angezeigt. Wird ein einzelner Job requestet, reicht
    // momentan die Authentifizerung, da man die Job-ID (UUID) kennen muss.
//    @Bean
//    FilterRegistrationBean<JobAuthorizationFilter> jobAuthFilter(JobAuthorizationFilter authorizationFilter) {
//        FilterRegistrationBean<JobAuthorizationFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(authorizationFilter);
//        registrationBean.addUrlPatterns("/api/v1/jobs/*");
//        return registrationBean;
//    }

    
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

//    @Bean
//    ApiKeyAuthenticationManager1 authenticationManager() {
//        return new ApiKeyAuthenticationManager1(objectContext, encoder);
//    }
//    
//    @Bean
//    ApiKeyAuthFilter1 authenticationFilter() {
//        ApiKeyAuthFilter1 filter = new ApiKeyAuthFilter1(apiKeyHeaderName);
//        filter.setAuthenticationManager(authenticationManager());
//        return filter;
//    }
    
    // Funktioniert es mit zweitenm Filter, der Query-Param ausliest (z.B)?
    // Mir wäre aber Formlogin fast lieber, dann müsste es aber wohl unter
    // anderer URL laufen. formLogin and key-auth kombiniert, geht das?
    
    // Vielleicht wenn wir den EntryPoint schlauer machen: dort unterscheiden
    // was accept header ist.
    
    // Oder so: https://stackoverflow.com/questions/33739359/combining-basic-authentication-and-form-login-for-the-same-rest-api
    
    @Autowired
    private ApiKeyHeaderAuthenticationService apiKeyHeaderAuthService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {                
        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityMatcher("/api/**", "/protected/**")
//                .formLogin(AbstractHttpConfigurer::disable)
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .permitAll())
//                .addFilter(authenticationFilter())
//                .formLogin(withDefaults())
                
                // permitAll() isn't the same as no security and skip all filters
                // D.h. der ApiKeyHeaderAuthenticationFilter wird trotzdem ausgeführt.
//                .authorizeHttpRequests(registry -> registry
//                        .requestMatchers(AntPathRequestMatcher.antMatcher("/public/**")).permitAll()
//                        .anyRequest().authenticated()
//                )
                .addFilterBefore(new ApiKeyHeaderAuthenticationFilter(authenticationManager(), apiKeyHeaderName), LogoutFilter.class)
                
                // Überschreibt auch Weiterleitung zu Default-Login-Seite, falls
                // formLogin(withDefaults()) aktiviert ist.

                // FIXME Während refactoring ausschalten. 
//                .exceptionHandling(exceptionHandling ->
//                    exceptionHandling.authenticationEntryPoint(authEntryPoint)
//                )
                .logout(AbstractHttpConfigurer::disable)
                .build();
    }
    
    @Bean
    AuthenticationManager authenticationManager() {
        ApiKeyHeaderAuthenticationProvider apiKeyHeaderAuthenticationProvider = new ApiKeyHeaderAuthenticationProvider(apiKeyHeaderAuthService);
        //TenantAuthenticationProvider tenantAuthenticationProvider = new TenantAuthenticationProvider(tenantAuthService);
        return new ProviderManager(apiKeyHeaderAuthenticationProvider /*, tenantAuthenticationProvider*/);
    }

}
