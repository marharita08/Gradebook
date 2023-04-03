package org.example.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private String dbNameParameter = "dbName";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);
        String dbName = obtainDBName(request);
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        if (dbName == null) {
            dbName = "";
        }

        String usernameDBName = String.format("%s%s%s", username.trim(), String.valueOf(Character.LINE_SEPARATOR), dbName);
        UsernamePasswordAuthenticationToken authRequest = new  UsernamePasswordAuthenticationToken(usernameDBName, password);
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected String obtainDBName(HttpServletRequest request) {
        return request.getParameter(this.dbNameParameter);
    }

}
