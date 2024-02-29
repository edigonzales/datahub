package ch.so.agi.datahub.auth;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

import jakarta.servlet.http.HttpServletRequest;

public class ApiKeyAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

    private final String headerName;

    public ApiKeyAuthFilter(final String headerName) {
        this.headerName = headerName;
        
        // AbstractPreAuthenticatedProcessingFilter speichert SecurityContext in einer Session.
        // Obwohl man in "securityFilterChain" die ganze Geschichte als stateless definiert.
        // Das ist wohl nur gültig, wenn man keinen eigenen Filter macht, der explizit
        // eine Session macht.
        // Man kann auch "NullSecurityContextRepository" verwenden.
        setSecurityContextRepository(new RequestAttributeSecurityContextRepository());
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return request.getHeader(headerName);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        // No credentials when using API key
        return null;
    }

}
