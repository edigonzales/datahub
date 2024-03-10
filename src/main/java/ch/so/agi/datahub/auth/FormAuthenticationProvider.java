package ch.so.agi.datahub.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ch.so.agi.datahub.cayenne.CoreApikey;
import ch.so.agi.datahub.cayenne.CoreOrganisation;

@Component
public class FormAuthenticationProvider implements AuthenticationProvider {

    private ObjectContext objectContext;
    
    private PasswordEncoder encoder;
    
    private final FormUserDetailsService userDetailsService;
    
    public FormAuthenticationProvider(ObjectContext objectContext, PasswordEncoder encoder, FormUserDetailsService userDetailsService) {
        this.objectContext = objectContext;
        this.encoder = encoder;
        this.userDetailsService = userDetailsService;
    }
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            ApiKeyUser userDetails = userDetailsService.loadUserByUsername(authentication.getName());
            if (userDetails == null) {
                throw new UsernameNotFoundException("User not found");
            }
            
            CoreOrganisation organisation = ObjectSelect.query(CoreOrganisation.class)
                    .where(CoreOrganisation.ANAME.eq(userDetails.getUsername()))
                    .selectOne(objectContext);

            CoreApikey myApiKey = null;
            for (CoreApikey apiKey : organisation.getCoreApikeys()) {
                if ((apiKey.getDateofexpiry() == null || apiKey.getDateofexpiry().isAfter(LocalDateTime.now())) 
                        && apiKey.getRevokedat() == null) {
                    if (encoder.matches(authentication.getCredentials().toString(), apiKey.getApikey())) {
                        myApiKey = apiKey;
                        
                        List<GrantedAuthority> grants = new ArrayList<GrantedAuthority>();
                        grants.add(new SimpleGrantedAuthority(myApiKey.getCoreOrganisation().getArole()));
                        userDetails.setAuthorities(grants);

                        break;
                    }
                } else {
                    continue;
                }
            }
            
            if (myApiKey == null) {
                throw new BadCredentialsException("Invalid Credentials");
            } else {
                return new ApiKeyHeaderAuthenticationToken(authentication.getCredentials().toString(), userDetails);
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid Credentials");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // Muss UsernamePasswordAuthenticationToken sein, sonst wird Form-Login nicht unterstützt.
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
