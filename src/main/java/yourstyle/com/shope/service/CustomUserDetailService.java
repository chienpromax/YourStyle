package yourstyle.com.shope.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.CustomUserDetails;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Account account = accountService.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (account == null) {
            // System.out.println("User not found: " + usernameOrEmail);
            throw new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
        }
        
        // System.out.println("User found: " + account.getUsername());
        // System.out.println("Stored password: " + account.getPassword());
        // System.out.println("Stored role: " + account.getRole());
        return new CustomUserDetails(account);
    }
    
    

}
