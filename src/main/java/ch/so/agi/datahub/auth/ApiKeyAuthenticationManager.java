package ch.so.agi.datahub.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;

public class ApiKeyAuthenticationManager implements AuthenticationManager {
    
    
    // Aus Datenbank:
    
    String principalRequestValue = "1234";

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        
        String principal = (String) authentication.getPrincipal();
        if (!Objects.equals(principalRequestValue, principal)) {
            throw new BadCredentialsException(
                    "The API key was not found or not the expected value.");
        }

        // Falls man hier mehr wissen will/muss.
//        List<GrantedAuthority> grants = new ArrayList<GrantedAuthority>();
//        grants.add(new SimpleGrantedAuthority("ROLE_USER"));
//        PreAuthenticatedAuthenticationToken tokenUser = new PreAuthenticatedAuthenticationToken(authentication.getName(), null, grants);
//        tokenUser.setDetails(authentication.getDetails());
//        tokenUser.setAuthenticated(true);

        authentication.setAuthenticated(true);

        return authentication;
    }
}
