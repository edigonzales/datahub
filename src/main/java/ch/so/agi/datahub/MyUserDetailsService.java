package ch.so.agi.datahub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("password"));
        
        String token = tokenService.getAccessToken();
        System.out.println("token: " + token);
        
        
        
        if (username.equalsIgnoreCase("user")) {
            
            System.out.println("User gefunden...");
            
            return new MyUserPrincipal(user);
        } else {
            System.out.println("user not found...");
            throw new UsernameNotFoundException(username);
        }
    }
}
