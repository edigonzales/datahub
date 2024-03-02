package ch.so.agi.datahub.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;

import ch.so.agi.datahub.cayenne.CoreApikey;

public class ApiKeyAuthenticationManager implements AuthenticationManager {
    
    private ObjectContext objectContext;
    
    private PasswordEncoder encoder;
    
    public ApiKeyAuthenticationManager(ObjectContext objectContext, PasswordEncoder encoder) {
        this.objectContext = objectContext;
        this.encoder = encoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String principal = (String) authentication.getPrincipal();
           
        List<CoreApikey> apiKeys = ObjectSelect.query(CoreApikey.class)
                .where(CoreApikey.REVOKEDAT.isNull())
                .and(CoreApikey.DATEOFEXPIRY.gt(LocalDateTime.now()).orExp(CoreApikey.DATEOFEXPIRY.isNull()))
                .select(objectContext);

        // Weil das Passwort randommässig gesalted wird, muss man die matches-Funktion verwenden und
        // kann nicht den Plaintext-Key nochmals encoden und mit der DB vergleichen.
        CoreApikey myApiKey = null;
        for (CoreApikey apiKey : apiKeys) {
            if (encoder.matches(principal, apiKey.getApikey())) {
                myApiKey = apiKey;
                break;
            }
        }
        
        if (apiKeys.size() == 0 || myApiKey == null) {
            throw new BadCredentialsException(
                    "The API key was not found or not the expected value.");
        }  

        List<GrantedAuthority> grants = new ArrayList<GrantedAuthority>();
        grants.add(new SimpleGrantedAuthority(myApiKey.getCoreOrganisation().getArole()));
        PreAuthenticatedAuthenticationToken tokenUser = new PreAuthenticatedAuthenticationToken(authentication.getName(), null, grants);
        tokenUser.setDetails(myApiKey);
        tokenUser.setAuthenticated(true);

        return tokenUser;
    }
}
