package com.d2d.securityConfig;

import com.d2d.entity.Users;
import com.d2d.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private AuthService authService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = null;
        
        try {
            users = authService.getUser(username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<GrantedAuthority> listRole = new ArrayList<>();

        return new User(users.getUsername(), users.getPassword(), listRole);
    }

}
