package org.example;

import org.apache.log4j.PropertyConfigurator;
import org.example.dao.ConnectionPool;
import org.example.dao.GenerateTables;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.*;
import java.util.Properties;

public class MyServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        InputStream stream =
                Thread.currentThread().getContextClassLoader().getResourceAsStream("log4j.properties");
        Properties properties = new Properties();
        try {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PropertyConfigurator.configure(properties);
        GenerateTables generateTables = new GenerateTables(new ConnectionPool());
        generateTables.generate();

        /*PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = new User();
        Properties userProp = new Properties();
        try {
            userProp.load(
                    Thread.currentThread()
                            .getContextClassLoader()
                            .getResourceAsStream("defaultAdmin.properties"));
            user.setPassword(passwordEncoder.encode(userProp.getProperty("password")));
            user.setUsername(userProp.getProperty("username"));
            generateTables.initDefaultUser(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
