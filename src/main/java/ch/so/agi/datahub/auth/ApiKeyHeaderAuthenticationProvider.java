package ch.so.agi.datahub.auth;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class ApiKeyHeaderAuthenticationProvider implements AuthenticationProvider {

    private final ApiKeyHeaderAuthService authService;

    public ApiKeyHeaderAuthenticationProvider(ApiKeyHeaderAuthService service) {
        this.authService = service;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        System.out.println("*** provider authentication");
        return authService.authenticate((ApiKeyHeaderAuthenticationToken) authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(ApiKeyHeaderAuthenticationToken.class);
    }

}
