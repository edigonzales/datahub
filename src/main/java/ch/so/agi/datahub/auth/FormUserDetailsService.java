package ch.so.agi.datahub.auth;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class FormUserDetailsService implements UserDetailsService {

   private PasswordEncoder encoder;
    
    public FormUserDetailsService(PasswordEncoder encoder) {
        this.encoder = encoder;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails ramesh = User.builder()
                .username("ramesh")
                .password(encoder.encode("password"))
                .roles("USER")
                .build();

        return ramesh;
    }

}
