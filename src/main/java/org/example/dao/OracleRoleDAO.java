package org.example.dao;

import org.apache.log4j.Logger;
import org.example.entities.Role;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
public class OracleRoleDAO implements RoleDAO {
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private Connection connection;
    private static final Logger LOGGER = Logger.getLogger(OracleRoleDAO.class.getName());

    /**
     * Read roles for user from database and put them into set.
     * @param id user id
     * @return Set<Role>
     */
    @Override
    public Set<Role> getRolesByUser(int id) {
        Set<Role> set = new HashSet<>();
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Reading roles for user " + id + " from database.");
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_ROLE" +
                            " join LAB3_ROZGHON_USER_ROLE using(ROLE_ID) where USER_ID=?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing roles and put them into set.");
            while (resultSet.next()) {
                set.add(parseRole(resultSet));
            }
            LOGGER.info("Set of roles complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
        return set;
    }

    private Role parseRole(ResultSet resultSet) {
        Role role = null;
        try {
            LOGGER.info("Parsing result set into Role.");
            int id = resultSet.getInt("ROLE_ID");
            String name = resultSet.getString("NAME");
            role = new Role(id, name);
            LOGGER.info("Parsing complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return role;
    }

    /**
     * Get role from database by id.
     * @param id role id
     * @return Role
     */
    @Override
    public Role getRoleByID(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        Role role = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_ROLE"
                            + " where ROLE_ID=?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                role = parseRole(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll();
        }
        return role;
    }

    /**
     * Read all roles from database and put them into set.
     * @return Set<Role>
     */
    public Set<Role> getAllRoles() {
        Set<Role> set = new HashSet<>();
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Reading all roles from database.");
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_ROLE order by ROLE_ID");
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing roles and put them into set.");
            while (resultSet.next()) {
                set.add(parseRole(resultSet));
            }
            LOGGER.info("Set of roles complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
        return set;
    }

    private void closeAll() {
        if (resultSet != null) {
            try {
                LOGGER.info("Closing result set.");
                resultSet.close();
                LOGGER.info("Result set closed.");
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        if (preparedStatement != null) {
            try {
                LOGGER.info("Closing statement.");
                preparedStatement.close();
                LOGGER.info("Statement closed.");
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        if (connection != null) {
            try {
                LOGGER.info("Closing connection.");
                connection.close();
                LOGGER.info("Connection closed.");
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
