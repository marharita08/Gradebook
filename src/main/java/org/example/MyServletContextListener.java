package org.example;

import org.apache.log4j.PropertyConfigurator;
import org.example.dao.GenerateTables;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

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
        WebApplicationContext context =
                WebApplicationContextUtils.getRequiredWebApplicationContext(servletContextEvent.getServletContext());
        GenerateTables generateTables = context.getBean(GenerateTables.class);
        generateTables.generate();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
