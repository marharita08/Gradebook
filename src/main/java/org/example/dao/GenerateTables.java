package org.example.dao;

import org.apache.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class GenerateTables {
    private static final Logger LOGGER = Logger.getLogger(GenerateTables.class.getName());
    private final ConnectionPool connectionPool;

    public GenerateTables(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void generate() {
        try (InputStream stream =
                     Thread.currentThread().getContextClassLoader().getResourceAsStream("init.sql")) {
            LOGGER.info("Reading from init.sql .");
            String sql = new BufferedReader(new InputStreamReader(stream))
                    .lines().collect(Collectors.joining("\n"));
            try (Connection connection = connectionPool.getConnection();
                 Statement stmt = connection.createStatement()) {
                LOGGER.info("Executing init.sql .");
                stmt.execute(sql);
            }
        } catch (SQLException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
