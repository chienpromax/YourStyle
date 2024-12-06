package yourstyle.com.shope.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import yourstyle.com.shope.model.Account;
import yourstyle.com.shope.model.CustomUserDetails;
import yourstyle.com.shope.repository.AccountRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Tên đăng nhập không tồn tại"));
        return new CustomUserDetails(account);
    }

}
