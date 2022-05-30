package org.example.dao;

import org.apache.log4j.Logger;
import org.example.entities.User;

import java.io.*;
import java.sql.*;
import java.util.stream.Collectors;

public class GenerateTables {
   /* private static final String INSERT_USER = "Insert into GRADEBOOK_USER (username, password) values (?, ?)";
    private static final String ADD_ROLE_TO_USER = "insert into USER_ROLE values (?, ?)";
    private static final String GET_COUNT_OF_USERS = "select count(USER_ID) as AMOUNT from GRADEBOOK_USER ";*/
    private static final Logger LOGGER = Logger.getLogger(GenerateTables.class.getName());
    private final ConnectionPool connectionPool;

    public GenerateTables(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void generate() {
        try (InputStream stream =
                     Thread.currentThread().getContextClassLoader().getResourceAsStream("initSchools.sql")) {
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

    /*public void initDefaultUser(User user) {
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_COUNT_OF_USERS);
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement preparedStatement1 = connection.prepareStatement(ADD_ROLE_TO_USER)) {
            resultSet.next();
            int count = resultSet.getInt("AMOUNT");
            if (count == 0) {
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.executeUpdate();
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                preparedStatement1.setInt(1, user.getId());
                preparedStatement1.setInt(2, 1);
                preparedStatement1.executeUpdate();
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }*/
}
