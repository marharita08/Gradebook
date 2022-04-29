package org.example.dao;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class ConnectionPool {

    private static final Logger LOGGER = Logger.getLogger(ConnectionPool.class.getName());

    /**
     * Getting connection using DataSource.
     * @return Connection
     */
    public Connection getConnection() {
        Connection conn = null;
        try {
            InitialContext initContext = new InitialContext();
            DataSource ds = (DataSource) initContext.lookup("Gradebook/jdbcDS");
            conn = ds.getConnection();
        } catch (NamingException | SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return conn;
    }
}
