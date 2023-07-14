package org.example.dao;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.*;
import java.sql.*;
import java.util.stream.Collectors;

@Component
public class GenerateTables {
    private static final Logger LOGGER = Logger.getLogger(GenerateTables.class.getName());
    private static final String INIT_FILE_NAME = "initSchools.sql";
    private final ConnectionPool connectionPool;

    public GenerateTables(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void generate() {
        try (InputStream stream =
                     Thread.currentThread().getContextClassLoader().getResourceAsStream(INIT_FILE_NAME)) {
            LOGGER.info("Reading from initSchools.sql .");
            String sql = new BufferedReader(new InputStreamReader(stream))
                    .lines().collect(Collectors.joining("\n"));
            try (Connection connection = connectionPool.getConnection();
                 Statement stmt = connection.createStatement()) {
                LOGGER.info("Executing initSchools.sql .");
                stmt.execute(sql);
            }
        } catch (SQLException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
