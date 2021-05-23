package org.example.dao;

import org.example.entities.Role;
import org.example.entities.User;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class OracleUserDAO implements UserDAO {
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private Connection connection;
    private OracleRoleDAO roleDAO;

    public OracleUserDAO(OracleRoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    /**
     * Get user from database by username.
     * @param username username
     * @return User
     */
    @Override
    public User getUserByUsername(String username) {
        connection = ConnectionPool.getInstance().getConnection();
        User user = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_USER"
                            + " where USERNAME=?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = parseUser(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return user;
    }

    /**
     * Get user from database by id.
     * @param id user id
     * @return User
     */
    @Override
    public User getUserByID(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        User user = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_USER"
                            + " where USER_ID=?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = parseUser(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return user;
    }

    /**
     * Read all users from database and put them into list.
     * @return List<User>
     */
    @Override
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        connection = ConnectionPool.getInstance().getConnection();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_USER order by user_id");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseUser(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return list;
    }

    private User parseUser(ResultSet resultSet) {
        User user = null;
        try {
            int id = resultSet.getInt("USER_ID");
            String username = resultSet.getString("USERNAME");
            String password = resultSet.getString("PASSWORD");
            Set<Role> roles = roleDAO.getRolesByUser(id);
            user = new User(id, username, password, roles);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return user;
    }


    /**
     * Insert new user into database.
     * @param user adding user
     */
    @Override
    public void addUser(User user) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql = "Insert into LAB3_ROZGHON_USER "
                + "values (LAB3_ROZGHON_USER_SEQ.nextval, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.executeUpdate();
            for (Role role:user.getRoles()) {
                sql = "Insert into LAB3_ROZGHON_USER_ROLE values (LAB3_ROZGHON_USER_SEQ.currval, ?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, role.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    /**
     * Update user's data into database.
     * @param user editing user
     */
    @Override
    public void updateUser(User user) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql = "UPDATE LAB3_ROZGHON_USER "
                + "set username = ?, password = ? where user_id = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setInt(3, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    /**
     * Delete user from database.
     * @param id user's id
     */
    @Override
    public void deleteUser(int id) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql;
        try {
            sql = "Delete from LAB3_ROZGHON_USER_ROLE "
                    + "where user_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            sql = "Delete from LAB3_ROZGHON_USER "
                    + "where user_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    /**
     * Delete user's role from database.
     * @param userID user's id
     * @param roleID role's id
     */
    //@Override
    public void deleteUserRole(int userID, int roleID) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql;
        try {
            sql = "Delete from LAB3_ROZGHON_USER_ROLE "
                    + "where user_id = ? and  role_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, roleID);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    /**
     * Add user's role.
     * @param userID user's id
     * @param roleID role's id
     */
    //@Override
    public void addUserRole(int userID, int roleID) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql;
        try {
            sql = "select count(user_id) AMOUNT from LAB3_ROZGHON_USER_ROLE" +
                    " where user_id=? and role_id=?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, roleID);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int n = resultSet.getInt("AMOUNT");
            if (n < 1) {
                sql = "insert into LAB3_ROZGHON_USER_ROLE "
                        + " values (?, ?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, userID);
                preparedStatement.setInt(2, roleID);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
    }

    /**
     * Get total count of users from database.
     * @return int
     */
    @Override
    public int getCountOfUsers() {
        connection = ConnectionPool.getInstance().getConnection();
        int count = 0;
        String sql = "select count(USER_ID) as AMOUNT " +
                "from LAB3_ROZGHON_USER ";
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            count = resultSet.getInt("AMOUNT");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return count;
    }

    /**
     * Get user list for page.
     * @param page number of page
     * @param range amount of users per page
     * @return List<User>
     */
    @Override
    public List<User> getUsersByPage(int page, int range) {
        List<User> list = new ArrayList<>();
        connection = ConnectionPool.getInstance().getConnection();
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM (SELECT p.*, ROWNUM rn FROM" +
                            " (SELECT * FROM LAB3_ROZGHON_TEACHER ORDER BY TEACHER_ID) p)" +
                            " WHERE rn BETWEEN ? AND ?");
            preparedStatement.setInt(1, (page - 1)*range + 1);
            preparedStatement.setInt(2, page*range);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(parseUser(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(resultSet, preparedStatement, connection);
        }
        return list;
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
