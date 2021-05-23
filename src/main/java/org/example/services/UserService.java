package org.example.services;

import org.example.dao.OracleUserDAO;
import org.example.entities.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private OracleUserDAO dao;

    public UserService(OracleUserDAO dao) {
        this.dao = dao;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        return dao.getUserByUsername(s);
    }
}
