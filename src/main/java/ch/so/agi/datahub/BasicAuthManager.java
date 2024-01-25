package ch.so.agi.datahub;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BasicAuthManager implements AuthenticationManager {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public BasicAuthManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String password = authentication.getCredentials().toString();
        
        //System.out.println("password: " + password);
        
        UserDetails user;

        try {
            user = userDetailsService.loadUserByUsername(username);
            
            System.out.println("security user: " + user);
            
        } catch (UsernameNotFoundException ex) {
            System.out.println("User does not exists");
            throw new BadCredentialsException("User does not exists");
        }

        if (password.isBlank() || !passwordEncoder.matches(password, user.getPassword())) {
            System.out.println("Password is wrong");
            throw new BadCredentialsException("Password is wrong");
        }

        return new UsernamePasswordAuthenticationToken(username, null, user.getAuthorities());
    }

}
