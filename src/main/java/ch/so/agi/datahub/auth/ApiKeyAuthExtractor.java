package ch.so.agi.datahub.auth;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class ApiKeyAuthExtractor {
    private String apiKey = "1234";

    public Optional<Authentication> extract(HttpServletRequest request) {
        String providedKey = request.getHeader("X-API-KEY");
        if (providedKey == null || !providedKey.equals(apiKey)) {
            return Optional.empty();            
        }

        System.out.println(providedKey);
        
        return Optional.of(new ApiKeyAuth(providedKey, AuthorityUtils.NO_AUTHORITIES));
    }

}
