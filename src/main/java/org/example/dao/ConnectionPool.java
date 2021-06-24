package org.example.dao;

import org.apache.log4j.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


public class ConnectionPool {

    private static ConnectionPool instance;
    private static final Logger LOGGER = Logger.getLogger(ConnectionPool.class.getName());

    private ConnectionPool() { }

    public static ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }

    /**
     * Getting connection using DataSource.
     * @return Connection
     */
    public Connection getConnection() {
        Connection conn = null;
        try {
            LOGGER.info("Getting connection with database.");
            InitialContext initContext = new InitialContext();
            DataSource ds = (DataSource) initContext.lookup("Gradebook/jdbcDS");
            conn = ds.getConnection();
            LOGGER.info("Connection established.");
        } catch (NamingException | SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return conn;
    }
}
