package org.example.dao;

import org.apache.log4j.Logger;
import org.example.entities.Role;
import org.example.entities.User;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;


@Component
public class OracleUserDAO implements UserDAO {
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private Connection connection;
    private OracleRoleDAO roleDAO;
    private static final Logger LOGGER = Logger.getLogger(OracleUserDAO.class.getName());

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
            LOGGER.info("Reading user " + username + " from database.");
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_USER"
                            + " where USERNAME=?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = parseUser(resultSet);
            }
            LOGGER.info("Reading complete");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
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
            LOGGER.info("Reading user " + id + " from database.");
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_USER"
                            + " where USER_ID=?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = parseUser(resultSet);
            }
            LOGGER.info("Reading complete");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
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
            LOGGER.info("Reading all users from database.");
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM LAB3_ROZGHON_USER order by user_id");
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing users and put them into list.");
            while (resultSet.next()) {
                list.add(parseUser(resultSet));
            }
            LOGGER.info("List of users complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
        return list;
    }

    private User parseUser(ResultSet resultSet) {
        User user = null;
        try {
            LOGGER.info("Parsing result set into User.");
            int id = resultSet.getInt("USER_ID");
            String username = resultSet.getString("USERNAME");
            String password = resultSet.getString("PASSWORD");
            Set<Role> roles = roleDAO.getRolesByUser(id);
            user = new User(id, username, password, roles);
            LOGGER.info("Parsing complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return user;
    }


    /**
     * Insert new user into database.
     * @param user adding user
     */
    @Override
    public void addUser(User user) throws Exception {
        User user1 = getUserByUsername(user.getUsername());
        if (user1 == null) {
            connection = ConnectionPool.getInstance().getConnection();
            LOGGER.info("Inserting user " + user.getUsername() + " into database.");
            String sql = "Insert into LAB3_ROZGHON_USER "
                    + "values (LAB3_ROZGHON_USER_SEQ.nextval, ?, ?)";
            try {
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.executeUpdate();
                LOGGER.info("Inserting complete.");
            } catch (SQLException throwables) {
                LOGGER.error(throwables.getMessage(), throwables);
            } finally {
                closeAll();
            }
        } else {
            Exception e = new  Exception("Username already taken.");
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Update user's data into database.
     * @param user editing user
     */
    @Override
    public void updateUser(User user) throws Exception {
        User user1 = getUserByUsername(user.getUsername());
        if(user1 == null || user1.getId() == user.getId()) {
            connection = ConnectionPool.getInstance().getConnection();
            LOGGER.info("Updating user " + user.getUsername() + ".");
            String sql = "UPDATE LAB3_ROZGHON_USER "
                    + "set username = ?, password = ? where user_id = ?";
            try {
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setInt(3, user.getId());
                preparedStatement.executeUpdate();
                LOGGER.info("Updating complete.");
            } catch (SQLException throwables) {
                LOGGER.error(throwables.getMessage(), throwables);
            } finally {
                closeAll();
            }
        } else {
            Exception e = new  Exception("Username already taken.");
            LOGGER.error(e.getMessage(), e);
            throw e;
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
        LOGGER.info("Deleting user " + id + ".");
        try {
            LOGGER.info("Deleting user's roles from database.");
            sql = "Delete from LAB3_ROZGHON_USER_ROLE "
                    + "where user_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting user from database.");
            sql = "Delete from LAB3_ROZGHON_USER "
                    + "where user_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting complete");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
    }

    /**
     * Delete user's role from database.
     * @param userID user's id
     * @param roleID role's id
     */
    @Override
    public void deleteUserRole(int userID, int roleID) {
        connection = ConnectionPool.getInstance().getConnection();
        String sql;
        try {
            LOGGER.info("Deleting role " + roleID + " from user " + userID + ".");
            sql = "Delete from LAB3_ROZGHON_USER_ROLE "
                    + "where user_id = ? and  role_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, roleID);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting complete");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
    }

    /**
     * Add user's role.
     * @param userID user's id
     * @param roleID role's id
     */
    @Override
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
                LOGGER.info("Adding role " + roleID + " to user " + userID);
                sql = "insert into LAB3_ROZGHON_USER_ROLE "
                        + " values (?, ?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, userID);
                preparedStatement.setInt(2, roleID);
                preparedStatement.executeUpdate();
                LOGGER.info("Role added.");
            } else {
                LOGGER.info("User " + userID + " already has role " + roleID);
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
    }

    /**
     * Get total count of users from database.
     * @return int
     */
    @Override
    public int getCountOfUsers() {
        connection = ConnectionPool.getInstance().getConnection();
        LOGGER.info("Counting users.");
        int count = 0;
        String sql = "select count(USER_ID) as AMOUNT " +
                "from LAB3_ROZGHON_USER ";
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            count = resultSet.getInt("AMOUNT");
            LOGGER.info("Counting complete");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
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
        LOGGER.info("Reading users for " + page + " page.");
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM (SELECT p.*, ROWNUM rn FROM" +
                            " (SELECT * FROM LAB3_ROZGHON_USER ORDER BY USER_ID) p)" +
                            " WHERE rn BETWEEN ? AND ?");
            preparedStatement.setInt(1, (page - 1)*range + 1);
            preparedStatement.setInt(2, page*range);
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing users and put them into list.");
            while (resultSet.next()) {
                list.add(parseUser(resultSet));
            }
            LOGGER.info("List of users complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
        return list;
    }

    /**
     * Search users by set parameter.
     * @param val text of searching
     * @param param parameter of searching
     * @return List<User>
     * @throws Exception if set parameter is wrong
     */
    @Override
    public List<User> searchUsers(String val, String param) throws Exception {
        List<User> list = new ArrayList<>();
        String sql;
        LOGGER.info("Checking parameter of searching.");
        switch (param) {
            case "username":
                sql = " SELECT * FROM LAB3_ROZGHON_USER where upper(USERNAME) like ? ORDER BY USER_ID";
                break;
            case "id":
                sql = " SELECT * FROM LAB3_ROZGHON_USER where user_id like ? ORDER BY USER_ID";
                break;
            case "roles":
                sql = " select distinct u.* from LAB3_ROZGHON_USER u " +
                        "join LAB3_ROZGHON_USER_ROLE ur on u.user_id=ur.user_id " +
                        "join LAB3_ROZGHON_ROLE r on r.role_id=ur.role_id " +
                        "where name like ? ORDER BY u.USER_ID";
                break;
            default:
                Exception e = new Exception("Wrong parameter");
                LOGGER.error(e.getMessage(), e);
                throw e;
        }
        connection = ConnectionPool.getInstance().getConnection();
        try {
            LOGGER.info("Searching users by " + param + ".");
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "%" + val.toUpperCase(Locale.ROOT) + "%");
            resultSet = preparedStatement.executeQuery();
            LOGGER.info("Parsing users and put them into list.");
            while (resultSet.next()) {
                list.add(parseUser(resultSet));
            }
            LOGGER.info("List of users complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        } finally {
            closeAll();
        }
        return list;
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
