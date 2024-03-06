package ch.so.agi.datahub.auth;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class ApiKeyHeaderAuthenticationProvider implements AuthenticationProvider {

    private final ApiKeyHeaderAuthenticationService authenticationService;

    public ApiKeyHeaderAuthenticationProvider(ApiKeyHeaderAuthenticationService service) {
        this.authenticationService = service;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return authenticationService.authenticate((ApiKeyHeaderAuthenticationToken) authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(ApiKeyHeaderAuthenticationToken.class);
    }

}
