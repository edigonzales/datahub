package ch.so.agi.datahub.auth;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class ApiKeyHeaderAuthenticationToken implements Authentication {
    private boolean isAuthenticated;
    private AppUser userDetails;

    private final String apiKey;

    // Constructor to be used pre-authentication
    public ApiKeyHeaderAuthenticationToken(String apiKey) {
        this.apiKey = apiKey;
    }

    // Constructor to be used after successful authentication
    public ApiKeyHeaderAuthenticationToken(String apiKey, AppUser userDetails) {
        this.apiKey = apiKey;
        this.userDetails = userDetails;
        this.isAuthenticated = true;
    }
    
    public String getApiKey() {
        return apiKey;
    }

    @Override
    public String getName() {
        return userDetails.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return apiKey;
    }

    @Override
    public Object getDetails() {
        return userDetails;
    }

    @Override
    public Object getPrincipal() {
        return userDetails;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalArgumentException("Not supported, use constructor");
    }

}
