package org.example.security;

import org.apache.log4j.Logger;
import org.example.entities.User;
import org.example.services.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;

public class AuthProvider implements AuthenticationProvider {

    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private static final Logger LOGGER = Logger.getLogger(AuthProvider.class.getName());

    public AuthProvider(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        User user = (User) userService.loadUserByUsername(username);
        LOGGER.info("Checking username.");
        if(user != null && user.getUsername().equals(username)) {
            LOGGER.info("Username found.");
            LOGGER.info("Checking password.");
            if(!passwordEncoder.matches(password, user.getPassword())) {
                BadCredentialsException e = new BadCredentialsException("Wrong password");
                LOGGER.error(e.getMessage(), e);
                throw e;
            }
            LOGGER.info("Password correct.");
            Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
            return new UsernamePasswordAuthenticationToken(user, password, authorities);
        } else {
            BadCredentialsException e = new BadCredentialsException("Username not found");
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass));
    }
}
