package org.example.dao;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@PropertySource("classpath:datasource.properties")
public class ConnectionPool {

    private static final Logger LOGGER = Logger.getLogger(ConnectionPool.class.getName());

    @Value("${datasource.jndiname}")
    private String dataSourceJNDIName;

    @Value("${app.name}")
    private String appName;

    /**
     * Getting connection using DataSource.
     * @return Connection
     */
    public Connection getConnection() {
        Connection conn = null;
        try {
            InitialContext initContext = new InitialContext();
            DataSource ds = (DataSource) initContext.lookup(dataSourceJNDIName);
            conn = ds.getConnection();
        } catch (NamingException | SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return conn;
    }

    public Connection getConnection(String dbName) {
        Connection conn = null;
        try {
            InitialContext initContext = new InitialContext();
            String JNDIName = appName + "/" + dbName;
            DataSource ds = (DataSource) initContext.lookup(JNDIName);
            conn = ds.getConnection();
        } catch (NamingException | SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return conn;
    }
}
