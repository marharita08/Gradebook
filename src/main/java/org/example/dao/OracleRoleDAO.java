package org.example.dao;

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

    /**
     * Read roles for user from database and put them into set.
     * @param id user id
     * @return Set<Role>
     */
    public Set<Role> getRolesByUser(int id) {
        Set<Role> set = new HashSet<>();
        connection = ConnectionPool.getInstance().getConnection();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_ROLE" +
                            " join LAB3_ROZGHON_USER_ROLE using(ROLE_ID) where USER_ID=?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                set.add(parseRole(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
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
            throwables.printStackTrace();
        }
        return role;
    }

    /**
     * Get role from database by id.
     * @param id role id
     * @return Role
     */
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
            closeAll(resultSet, preparedStatement, connection);
        }
        return role;
    }

    public Set<Role> getAllRoles() {
        Set<Role> set = new HashSet<>();
        connection = ConnectionPool.getInstance().getConnection();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_ROLE");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                set.add(parseRole(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return set;
    }

    private void closeAll(ResultSet resultSet, PreparedStatement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
