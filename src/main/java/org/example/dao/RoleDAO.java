package org.example.dao;

import org.example.entities.Role;

import java.util.Set;

public interface RoleDAO {
    /**
     * Read roles for user from database and put them into set.
     * @param id user id
     * @return Set<Role>
     */
    Set<Role> getRolesByUser(int id);

    /**
     * Read all roles from database and put them into set.
     * @return Set<Role>
     */
    Set<Role> getAllRoles();

}
