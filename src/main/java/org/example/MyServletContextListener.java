package org.example;

import org.example.dao.GenerateTables;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MyServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        GenerateTables generateTables = new GenerateTables();
        generateTables.generate();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
