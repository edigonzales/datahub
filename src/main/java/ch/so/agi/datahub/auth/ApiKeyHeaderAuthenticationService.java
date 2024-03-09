package ch.so.agi.datahub.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ch.so.agi.datahub.cayenne.CoreApikey;

@Service
public class ApiKeyHeaderAuthenticationService {
    
    private ObjectContext objectContext;
    
    private PasswordEncoder encoder;

    public ApiKeyHeaderAuthenticationService(ObjectContext objectContext, PasswordEncoder encoder) {
        this.objectContext = objectContext;
        this.encoder = encoder;
    }
    
    public ApiKeyHeaderAuthenticationToken authenticate(ApiKeyHeaderAuthenticationToken apiKeyAuthenticationToken) {        
        List<CoreApikey> apiKeys = ObjectSelect.query(CoreApikey.class)
                .where(CoreApikey.REVOKEDAT.isNull())
                .and(CoreApikey.DATEOFEXPIRY.gt(LocalDateTime.now()).orExp(CoreApikey.DATEOFEXPIRY.isNull()))
                .select(objectContext);

        // Weil das Passwort randommässig gesalted wird, muss man die matches-Funktion verwenden und
        // kann nicht den Plaintext-Key nochmals encoden und mit der DB mittels SQL vergleichen.
        CoreApikey myApiKey = null;
        for (CoreApikey apiKey : apiKeys) {
            if (encoder.matches(apiKeyAuthenticationToken.getApiKey(), apiKey.getApikey())) {
                myApiKey = apiKey;
                break;
            }
        }

        if (apiKeys.size() > 0 && myApiKey != null) {
            ApiKeyUser authenticatedUser = new ApiKeyUser(myApiKey.getCoreOrganisation().getAname());
            
            List<GrantedAuthority> grants = new ArrayList<GrantedAuthority>();
            grants.add(new SimpleGrantedAuthority(myApiKey.getCoreOrganisation().getArole()));
            authenticatedUser.setAuthorities(grants);
            
            return new ApiKeyHeaderAuthenticationToken(apiKeyAuthenticationToken.getApiKey(), authenticatedUser);
        }

        return apiKeyAuthenticationToken;
    }

}
