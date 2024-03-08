package ch.so.agi.datahub.auth;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

//@Service
public class FormUserDetailsService_1 implements UserDetailsService {

    private PasswordEncoder encoder;
    
    public FormUserDetailsService_1(PasswordEncoder encoder) {
        this.encoder = encoder;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        
        System.out.println(username);
        
        
        List<GrantedAuthority> grants = new ArrayList<GrantedAuthority>();
        grants.add(new SimpleGrantedAuthority("foo"));

        return new org.springframework.security.core.userdetails.User("stefan", encoder.encode("ziegler"), grants);
    }

}
