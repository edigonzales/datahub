package ch.so.agi.datahub.auth;

import org.apache.cayenne.ObjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
//@EnableMethodSecurity
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

        
    @Autowired
    private ApiKeyHeaderAuthenticationService apiKeyHeaderAuthService;

    @Bean
    AuthenticationManager authenticationManager() {
        ApiKeyHeaderAuthenticationProvider apiKeyHeaderAuthenticationProvider = new ApiKeyHeaderAuthenticationProvider(apiKeyHeaderAuthService);
        //TenantAuthenticationProvider tenantAuthenticationProvider = new TenantAuthenticationProvider(tenantAuthService);
        return new ProviderManager(apiKeyHeaderAuthenticationProvider /*, tenantAuthenticationProvider*/);
    }

    @Bean
    @Order(1)
    SecurityFilterChain apiKeySecurityFilterChain(HttpSecurity http) throws Exception {                
        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> 
                    sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .securityMatcher("/api/v1/keys/**", "/api/v1/deliveries/**", "/api/v1/jobs/**", "/protected/**")
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> registry
                        // permitAll() isn't the same as no security and skip all filters
                        // D.h. der ApiKeyHeaderAuthenticationFilter wird trotzdem ausgeführt.
                        // Wenn man das nicht will, muss man mit securityMatcher feingranularer definieren
                        // was wie geschützt sein soll. Hilft auch, wenn man eine zweite 
                        // SecurityFilterChain hat.
                        //.requestMatchers(AntPathRequestMatcher.antMatcher("/public/**")).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new ApiKeyHeaderAuthenticationFilter(authenticationManager(), apiKeyHeaderName), LogoutFilter.class)
                // FIXME Während refactoring ausschalten. 
//                .exceptionHandling(exceptionHandling ->
//                    exceptionHandling.authenticationEntryPoint(authEntryPoint)
//                )
                .logout(AbstractHttpConfigurer::disable)
                .build();
    }
    
    @Autowired
    private FormAuthenticationProvider formAuthenticationProvider;

    @Bean
    @Order(2)
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .securityMatcher("/web/**")
            .authorizeHttpRequests((authorize) ->
                authorize.anyRequest().authenticated()
            )
            .authenticationProvider(formAuthenticationProvider)
            .formLogin(form -> form
                    .loginPage("/web/login")
                    // .usernameParameter("phone-number").passwordParameter("password")
                    .loginProcessingUrl("/web/login")
                    .defaultSuccessUrl("/web/welcome")
                    .permitAll()
            )
            .logout(logout -> logout
                    .logoutRequestMatcher(new AntPathRequestMatcher("/web/logout"))
                    .permitAll()
                );
        return http.build();
    }
 
}
