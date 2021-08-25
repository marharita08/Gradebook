package org.example.security;

import org.example.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserService userService;

    @Bean
    PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers(  "/addPupil/*", "/addClass/*", "/addSchoolYear/*", "/addSemester/*",
                        "/addSubject/*", "/addSubjectDetails/*", "/addUser/*",
                        "/editPupil/*", "/editClass/*", "/editSchoolYear/*", "/editSemester/*",
                        "/editSubject/*", "/editSubjectDetails/*",
                        "/deletePupil/*", "deleteClass/*", "deleteSchoolYear/*", "deleteSemester/*",
                        "/deleteSubject/*", "/deleteSubjectDetails/*", "/deleteUser/*",
                        "/viewAllUsers", "/searchUsers", "/deleteRole/**", "/addRole/**",
                        "/viewAllSubjectDetails", "viewAllPupils").hasAuthority("ADMIN")
                .antMatchers("/addLesson/*", "/addMark/*", "/addTheme/*",
                        "/editLesson/*", "/editMark/*", "/editTheme/*",
                        "/deleteLesson/*", "/deleteMark/*", "/deleteTheme/*").hasAuthority("TEACHER")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .defaultSuccessUrl("/")
                .and().logout().logoutUrl("/logout");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new AuthProvider(userService, passwordEncoder()));
    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }
}
