package ch.so.agi.datahub.auth;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class FormAuthenticationProvider implements AuthenticationProvider {

    private final FormUserDetailsService userDetailsService;
    
    public FormAuthenticationProvider(FormUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            
            System.out.println("password from form: " + authentication.getCredentials());
            
            // https://medium.com/@hari1036/building-custom-authentication-provider-in-spring-security-a-step-by-step-guide-af0e39ccc35d
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
            return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("Invalid Credentials");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
