package org.example.dao.postgres;

import org.apache.log4j.Logger;
import org.example.dao.ConnectionPool;
import org.example.dao.interfaces.PupilDAO;
import org.example.dao.interfaces.RoleDAO;
import org.example.dao.interfaces.TeacherDAO;
import org.example.dao.interfaces.UserDAO;
import org.example.entities.Role;
import org.example.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PostgresUserDAO implements UserDAO {

    private static final String GET_ALL_USERS = "SELECT * FROM GRADEBOOK_USER order by user_id";
    private static final String GET_USER_BY_USERNAME = "SELECT * FROM GRADEBOOK_USER where USERNAME=?";
    private static final String GET_USER = "SELECT * FROM GRADEBOOK_USER where USER_ID=?";
    private static final String INSERT_USER = "Insert into GRADEBOOK_USER (username, password, dbname) values (?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE GRADEBOOK_USER set username = ?, password = ? where user_id = ?";
    private static final String DELETE_USER = "Delete from GRADEBOOK_USER where user_id = ?";
    private static final String DELETE_ROLE_OF_USER = "Delete from USER_ROLE where user_id = ? and  role_id = ?";
    private static final String CHECK_ROLE_OF_USER = "select count(user_id) AMOUNT from USER_ROLE where user_id=? and role_id=?";
    private static final String ADD_ROLE_TO_USER = "insert into USER_ROLE values (?, ?)";
    private static final String GET_COUNT_OF_USERS = "select count(USER_ID) as AMOUNT from GRADEBOOK_USER ";
    private static final String GET_USERS_BY_PAGE = " SELECT * FROM GRADEBOOK_USER ORDER BY USER_ID limit ? offset ? ";
    private static final String SEARCH_USERS_BY_ID = " SELECT * FROM GRADEBOOK_USER where to_char(user_id, '99999') like ? ORDER BY USER_ID";
    private static final String SEARCH_USERS_BY_NAME = " SELECT * FROM GRADEBOOK_USER where upper(USERNAME) like ? ORDER BY USER_ID";
    private static final String SEARCH_USERS_BY_ROLES = " select distinct u.* from GRADEBOOK_USER u join USER_ROLE ur on u.user_id=ur.user_id " +
            "join ROLE r on r.role_id=ur.role_id where name like ? ORDER BY u.USER_ID";
    private final ConnectionPool connectionPool;
    private final RoleDAO roleDAO;
    private final TeacherDAO teacherDAO;
    private final PupilDAO pupilDAO;
    private static final Logger LOGGER = Logger.getLogger(PostgresUserDAO.class.getName());

    public PostgresUserDAO(ConnectionPool connectionPool, RoleDAO roleDAO, TeacherDAO teacherDAO, PupilDAO pupilDAO) {
        this.connectionPool = connectionPool;
        this.roleDAO = roleDAO;
        this.teacherDAO = teacherDAO;
        this.pupilDAO = pupilDAO;
    }

    /**
     * Get user from database by username.
     * @param username username
     * @return User
     */
    @Override
    public User getUserByUsername(String username, String dbName) {
        LOGGER.info("Reading user " + username + " from database.");
        User user = null;
        try (Connection connection = connectionPool.getConnection(dbName);
             PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_BY_USERNAME)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = parseUser(resultSet);
                }
                LOGGER.info("Reading complete");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return user;
    }

    /**
     * Get user from database by id.
     * @param id user id
     * @return User
     */
    @Override
    public User getUserByID(int id, String dbName) {
        LOGGER.info("Reading user " + id + " from database.");
        User user = null;
        try (Connection connection = connectionPool.getConnection(dbName);
             PreparedStatement preparedStatement = connection.prepareStatement(GET_USER)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = parseUser(resultSet);
                }
                LOGGER.info("Reading complete");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return user;
    }

    /**
     * Read all users from database and put them into list.
     * @return List<User>
     */
    @Override
    public List<User> getAllUsers(String dbName) {
        LOGGER.info("Reading all users from database.");
        List<User> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection(dbName);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_ALL_USERS)) {
            while (resultSet.next()) {
                list.add(parseUser(resultSet));
            }
            LOGGER.info("List of users complete.");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }

    private User parseUser(ResultSet resultSet) {
        User user = null;
        try {
            int id = resultSet.getInt("USER_ID");
            String username = resultSet.getString("USERNAME");
            String password = resultSet.getString("PASSWORD");
            String dbName = resultSet.getString("dbName");
            Set<Role> roles = roleDAO.getRolesByUser(id, dbName);
            user = new User(id, username, password, roles, dbName);
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
    public void addUser(User user, String dbName) throws Exception {
        User user1 = getUserByUsername(user.getUsername(), dbName);
        if (user1 == null) {
            LOGGER.info("Inserting user " + user.getUsername() + " into database.");
            try (Connection connection = connectionPool.getConnection(dbName);
                 PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER)) {
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setString(3, dbName);
                preparedStatement.executeUpdate();
                LOGGER.info("Inserting complete.");
            } catch (SQLException throwables) {
                LOGGER.error(throwables.getMessage(), throwables);
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
    public void updateUser(User user, String dbName) throws Exception {
        User user1 = getUserByUsername(user.getUsername(), dbName);
        if(user1 == null || user1.getId() == user.getId()) {
            LOGGER.info("Updating user " + user.getUsername() + ".");
            try (Connection connection = connectionPool.getConnection(dbName);
                 PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER)) {
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setInt(3, user.getId());
                preparedStatement.executeUpdate();
                LOGGER.info("Updating complete.");
            } catch (SQLException throwables) {
                LOGGER.error(throwables.getMessage(), throwables);
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
    public void deleteUser(int id, String dbName) {
        LOGGER.info("Deleting user " + id + ".");
        try (Connection connection = connectionPool.getConnection(dbName);
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER)) {
            LOGGER.info("Deleting user from database.");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting complete");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Delete user's role from database.
     * @param userID user's id
     * @param roleID role's id
     */
    @Override
    public void deleteUserRole(int userID, int roleID, String dbName) {
        LOGGER.info("Deleting role " + roleID + " from user " + userID + ".");
        try (Connection connection = connectionPool.getConnection(dbName);
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ROLE_OF_USER)) {
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, roleID);
            preparedStatement.executeUpdate();
            LOGGER.info("Deleting complete");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Add user's role.
     * @param userID user's id
     * @param roleID role's id
     */
    @Override
    public void addUserRole(int userID, int roleID, String dbName) {
        try (Connection connection = connectionPool.getConnection(dbName);
             PreparedStatement preparedStatement = connection.prepareStatement(CHECK_ROLE_OF_USER)) {
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, roleID);
            int n;
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                n = resultSet.getInt("AMOUNT");
            }
            if (n < 1) {
                LOGGER.info("Adding role " + roleID + " to user " + userID);
                try (PreparedStatement preparedStatement1 = connection.prepareStatement(ADD_ROLE_TO_USER)) {
                    preparedStatement1.setInt(1, userID);
                    preparedStatement1.setInt(2, roleID);
                    preparedStatement1.executeUpdate();
                    LOGGER.info("Role added.");
                }
            } else {
                LOGGER.info("User " + userID + " already has role " + roleID);
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
    }

    /**
     * Get total count of users from database.
     * @return int
     */
    @Override
    public int getCountOfUsers(String dbName) {
        LOGGER.info("Counting users.");
        int count = 0;
        try (Connection connection = connectionPool.getConnection(dbName);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_COUNT_OF_USERS)) {
            resultSet.next();
            count = resultSet.getInt("AMOUNT");
            LOGGER.info("Counting complete");
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
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
    public List<User> getUsersByPage(int page, int range, String dbName) {
        LOGGER.info("Reading users for " + page + " page.");
        List<User> list = new ArrayList<>();
        try (Connection connection = connectionPool.getConnection(dbName);
             PreparedStatement preparedStatement = connection.prepareStatement(GET_USERS_BY_PAGE)) {
            preparedStatement.setInt(1, range);
            preparedStatement.setInt(2, (page - 1) * range);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parseUser(resultSet));
                }
                LOGGER.info("List of users complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
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
    public List<User> searchUsers(String val, String param, String dbName) throws Exception {
        List<User> list = new ArrayList<>();
        String sql;
        LOGGER.info("Checking parameter of searching.");
        switch (param) {
            case "username":
                sql = SEARCH_USERS_BY_NAME;
                break;
            case "id":
                sql = SEARCH_USERS_BY_ID;
                break;
            case "roles":
                sql = SEARCH_USERS_BY_ROLES;
                break;
            default:
                Exception e = new Exception("Wrong parameter");
                LOGGER.error(e.getMessage(), e);
                throw e;
        }
        try (Connection connection = connectionPool.getConnection(dbName);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            LOGGER.info("Searching users by " + param + ".");
            preparedStatement.setString(1, "%" + val.toUpperCase(Locale.ROOT) + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(parseUser(resultSet));
                }
                LOGGER.info("List of users complete.");
            }
        } catch (SQLException throwables) {
            LOGGER.error(throwables.getMessage(), throwables);
        }
        return list;
    }
}
