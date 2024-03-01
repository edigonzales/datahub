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
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;

import ch.so.agi.datahub.cayenne.CoreApikey;

public class ApiKeyAuthenticationManager implements AuthenticationManager {
    
    
    // Aus Datenbank:
    
    String principalRequestValue = "1234";
    
    private ObjectContext objectContext;
    
    public ApiKeyAuthenticationManager(ObjectContext objectContext) {
        this.objectContext = objectContext;
    }
    

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        
        String principal = (String) authentication.getPrincipal();
        
        
//        Expression qualifier = ExpressionFactory.no
//                .containsIgnoreCaseExp(Author.NAME.getName(), "Paul")
//                .andExp(ExpressionFactory
//                  .endsWithExp(Author.NAME.getName(), "h"));

        
        CoreApikey apiKey = ObjectSelect.query(CoreApikey.class)
                .where(CoreApikey.REVOKEDAT.isNull()).and(CoreApikey.DATEOFEXPIRY.gt(LocalDateTime.now()).orExp(CoreApikey.DATEOFEXPIRY.isNull()))
                .selectOne(objectContext);
        
        System.out.println(apiKey);

        //.an.dot(Artist.DATE_OF_BIRTH).lt(LocalDate.of(1900,1,1))
        
        
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
