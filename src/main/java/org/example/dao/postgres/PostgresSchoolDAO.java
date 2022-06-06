package org.example.dao.postgres;

import org.apache.log4j.Logger;
import org.example.dao.ConnectionPool;
import org.example.dao.interfaces.SchoolDAO;
import org.example.entities.School;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PostgresSchoolDAO implements SchoolDAO {
    private static final String GET_SCHOOL = "SELECT * FROM schools where school_ID = ?";
    private static final String GET_SCHOOL_BY_NAME = "SELECT * FROM schools where name = ?";
    private static final String INSERT_SCHOOL = "Insert into schools (name) values (?)";
    private static final String UPDATE_SCHOOL = "UPDATE schools set name = ?, photo = ? where school_id = ?";
    private static final String DELETE_SCHOOL = "Delete from schools where school_ID = ?";
    private static final String GET_ALL_SCHOOLS = "select * from schools order by name";
    private static final Logger LOGGER = Logger.getLogger(PostgresSchoolDAO.class.getName());
    private final ConnectionPool connectionPool;

    public PostgresSchoolDAO(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    private School parseSchool(ResultSet resultSet) {
        School school = null;
        try {
            int id = resultSet.getInt("school_ID");
            String name = resultSet.getString("NAME");
            String photo = resultSet.getString("photo");
            school = new School(id, name, photo);
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return school;
    }

    @Override
    public School getSchool(int id) {
        LOGGER.info("Reading school " + id + " from database.");
        School school = null;
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_SCHOOL)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    school = parseSchool(resultSet);
                }
                LOGGER.info("Reading complete");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return school;
    }

    @Override
    public School getSchoolByName(String name) {
        LOGGER.info("Reading school " + name + " from database.");
        School school = null;
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_SCHOOL_BY_NAME)) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    school = parseSchool(resultSet);
                }
                LOGGER.info("Reading complete");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return school;
    }

    @Override
    public void createDB(String name) {
        LOGGER.info("Creating database " + name + ".");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("create database \"" + name + "\"")) {
            preparedStatement.executeUpdate();
            LOGGER.info("Creating complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }

    }

    @Override
    public void initTables(String dbName) {
        try (InputStream stream =
                     Thread.currentThread().getContextClassLoader().getResourceAsStream("init.sql")) {
            LOGGER.info("Reading from init.sql .");
            String sql = new BufferedReader(new InputStreamReader(stream))
                    .lines().collect(Collectors.joining("\n"));
            try (Connection connection = connectionPool.getConnection(dbName);
                 Statement stmt = connection.createStatement()) {
                LOGGER.info("Executing init.sql .");
                stmt.execute(sql);
            }
        } catch (SQLException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void addSchool(School school) {
        LOGGER.info("Inserting school  " + school.getName() + " into database.");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SCHOOL)) {
            preparedStatement.setString(1, school.getName());
            preparedStatement.executeUpdate();
            LOGGER.info("Inserting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    @Override
    public void updateSchool(School school) {
        LOGGER.info("Updating school " + school.getName() + ".");
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SCHOOL)) {
            preparedStatement.setString(1, school.getName());
            preparedStatement.setString(2, school.getPhoto());
            preparedStatement.setInt(3, school.getId());
            preparedStatement.executeUpdate();
            LOGGER.info("Updating complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    @Override
    public void deleteSchool(int id) {
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SCHOOL)) {
            LOGGER.info("Deleting subject " + id + ".");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    @Override
    public List<School> getAllSchools() {
        LOGGER.info("Reading all school from database.");
        List<School> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_ALL_SCHOOLS)) {
            while (resultSet.next()) {
                list.add(parseSchool(resultSet));
            }
            LOGGER.info("List of schools complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }


}
