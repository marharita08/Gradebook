package org.example.dao;

import org.example.entities.User;

import java.util.List;

public interface UserDAO {
    /**
     * Get user from database by username.
     * @param username username
     * @return User
     */
    User getUserByUsername(String username);

    /**
     * Read all users from database and put them into list.
     * @return List<User>
     */
    List<User> getAllUsers();

    /**
     * Insert new user into database.
     * @param user adding user
     */
    void addUser(User user) throws Exception;

    /**
     * Update user's data into database.
     * @param user editing user
     */
    void updateUser(User user) throws Exception;

    /**
     * Delete user from database.
     * @param id user's id
     */
    void deleteUser(int id);

    /**
     * Get total count of users from database.
     * @return int
     */
    int getCountOfUsers();

    /**
     * Get user list for page.
     * @param page number of page
     * @param range amount of users per page
     * @return List<User>
     */
    List<User> getUsersByPage(int page, int range);

    /**
     * Get user from database by id.
     * @param id user id
     * @return User
     */
    User getUserByID(int id);

    /**
     * Delete user's role from database.
     * @param userID user's id
     * @param roleID role's id
     */
    void deleteUserRole(int userID, int roleID);

    /**
     * Add user's role.
     * @param userID user's id
     * @param roleID role's id
     */
    void addUserRole(int userID, int roleID);

    /**
     * Search users by set parameter.
     * @param val text of searching
     * @param param parameter of searching
     * @return List<User>
     * @throws Exception if set parameter is wrong
     */
    List<User> searchUsers(String val, String param) throws Exception;
}
