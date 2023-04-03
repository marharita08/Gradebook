package org.example.services;

import org.example.dao.interfaces.UserDAO;
import org.example.entities.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserService implements UserDetailsService {
    private final UserDAO dao;

    public UserService(UserDAO dao) {
        this.dao = dao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        String[] usernameAndDBName = StringUtils.split(
                username, String.valueOf(Character.LINE_SEPARATOR));
        if (usernameAndDBName == null || usernameAndDBName.length != 2) {
            throw new UsernameNotFoundException("Username and dbName must be provided");
        }
        User user = dao.getUserByUsername(usernameAndDBName[0], usernameAndDBName[1]);
        if (user == null) {
            throw new UsernameNotFoundException(
                    String.format("Username not found for database, username=%s, dbName=%s",
                            usernameAndDBName[0], usernameAndDBName[1]));
        }
        return user;
    }
}
