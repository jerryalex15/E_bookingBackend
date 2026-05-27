package com.kode_project.ebooking.config;

import com.kode_project.ebooking.entity.User;
import com.kode_project.ebooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findUserByEmail(email); // username pour nous c'est email

        if (user == null)
            throw new UsernameNotFoundException("User not found!");

        List<GrantedAuthority> authorities = new ArrayList<>();
        GrantedAuthority grantedAuthority = new  SimpleGrantedAuthority(user.getRole().getRoleNom());
        authorities.add(grantedAuthority);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getMotDePasse(),
                user.isActiveStatut(),
                true,
                true,
                true,
                authorities
        );
    }
}
