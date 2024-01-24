package ch.so.agi.datahub;

import java.util.function.Supplier;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext requestContext) {
//        System.out.println(authentication.get().getCredentials());
//        System.out.println(authentication.get().getName());
//        System.out.println(((org.springframework.security.core.userdetails.User)authentication.get().getPrincipal()).getPassword());
//        
//        
//        System.out.println(requestContext.getVariables());
//        System.out.println(requestContext.getRequest().getAuthType());
//        System.out.println(requestContext.getRequest().getHeader("Authorization"));
        
        System.out.println("11");
        System.out.println(authentication.get().isAuthenticated());
        return new AuthorizationDecision(false);
    }
}
