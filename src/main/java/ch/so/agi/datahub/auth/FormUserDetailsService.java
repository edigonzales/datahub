package ch.so.agi.datahub.auth;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ch.so.agi.datahub.cayenne.CoreApikey;
import ch.so.agi.datahub.cayenne.CoreOrganisation;

@Service
public class FormUserDetailsService implements UserDetailsService {

    private ObjectContext objectContext;
    
    private PasswordEncoder encoder;
    
    public FormUserDetailsService(ObjectContext objectContext, PasswordEncoder encoder) {
        this.objectContext = objectContext;
        this.encoder = encoder;
    }
    
    @Override
    public ApiKeyUser loadUserByUsername(String username) throws UsernameNotFoundException {        
        CoreOrganisation organisation = ObjectSelect.query(CoreOrganisation.class)
                .where(CoreOrganisation.ANAME.eq(username))
                .selectOne(objectContext);
        
        if (organisation != null) {
            ApiKeyUser organisationUser = new ApiKeyUser(organisation.getAname());
            
            return organisationUser;            
        } else {
            return null;
        }
        
        
        
//        System.out.println(organisation);
//        
//        
//        
//        CoreApikey myApiKey = null;
//        for (CoreApikey apiKey : apiKeys) {
//            if (encoder.matches(username, apiKey.getApikey())) {
//                myApiKey = apiKey;
//                break;
//            }
//        }
//
//        if (apiKeys.size() > 0 && myApiKey != null) {
//            ApiKeyUser authenticatedUser = new ApiKeyUser(myApiKey.getCoreOrganisation().getAname());
//            
//            List<GrantedAuthority> grants = new ArrayList<GrantedAuthority>();
//            grants.add(new SimpleGrantedAuthority(myApiKey.getCoreOrganisation().getArole()));
//            authenticatedUser.setAuthorities(grants);
//
//            System.out.println(authenticatedUser);
//
////            return new ApiKeyHeaderAuthenticationToken(apiKeyAuthenticationToken.getApiKey(), authenticatedUser);
//            return authenticatedUser;
//        } else {
//            return null;
//        }

        
//        UserDetails ramesh = User.builder()
//                .username("ramesh")
//                .password(encoder.encode("password"))
//                .roles("USER")
//                .build();

    }

}
