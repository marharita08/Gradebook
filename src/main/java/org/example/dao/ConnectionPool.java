package org.example.dao;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


public class ConnectionPool {

    private static ConnectionPool instance;

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
            InitialContext initContext = new InitialContext();
            DataSource ds = (DataSource) initContext.lookup("Gradebook/jdbcDS");
            conn = ds.getConnection();
        } catch (NamingException | SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
