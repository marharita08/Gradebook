package org.example.dao;

import org.apache.log4j.Logger;
import org.example.entities.Role;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

@Repository
public class OracleRoleDAO implements RoleDAO {
    private static final String GET_ROLES_BY_USER = "SELECT * FROM LAB3_ROZGHON_ROLE" +
            " join LAB3_ROZGHON_USER_ROLE using(ROLE_ID) where USER_ID=?";
    private static final String GET_ROLE = "SELECT * FROM LAB3_ROZGHON_ROLE where ROLE_ID=?";
    private static final String GET_ALL_ROLES = "SELECT * FROM LAB3_ROZGHON_ROLE order by ROLE_ID";
    private final ConnectionPool connectionPool;
    private static final Logger LOGGER = Logger.getLogger(OracleRoleDAO.class.getName());

    public OracleRoleDAO(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    /**
     * Read roles for user from database and put them into set.
     * @param id user id
     * @return Set<Role>
     */
    @Override
    public Set<Role> getRolesByUser(int id) {
        LOGGER.info("Reading roles for user " + id + " from database.");
        Set<Role> set = new HashSet<>();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ROLES_BY_USER)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    set.add(parseRole(resultSet));
                }
                LOGGER.info("Set of roles complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return set;
    }

    private Role parseRole(ResultSet resultSet) {
        Role role = null;
        try {
            int id = resultSet.getInt("ROLE_ID");
            String name = resultSet.getString("NAME");
            role = new Role(id, name);
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
        LOGGER.info("Reading role " + id + " from database.");
        Role role = null;
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ROLE)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    role = parseRole(resultSet);
                }
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return role;
    }

    /**
     * Read all roles from database and put them into set.
     * @return Set<Role>
     */
    public Set<Role> getAllRoles() {
        Set<Role> set = new HashSet<>();
        LOGGER.info("Reading all roles from database.");
        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_ALL_ROLES)) {
            while (resultSet.next()) {
                set.add(parseRole(resultSet));
            }
            LOGGER.info("Set of roles complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return set;
    }
}
