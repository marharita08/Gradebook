package org.example.dao.interfaces;

import org.example.entities.School;

import java.util.List;

public interface SchoolDAO {
    /**
     * Read school from database by id.
     * @param id lesson id
     * @return School
     */
    School getSchool(int id);

    /**
     * Insert new school into database.
     * @param school adding school
     */
    void addSchool(School school);

    /**
     * Update school data into database.
     * @param school editing school
     */
    void updateSchool(School school);

    /**
     * Delete school from database.
     * @param id school id
     */
    void deleteSchool(int id);

    /**
     * Read school from database by theme.
     * @return List<School>
     */
    List<School> getAllSchools();

    School getSchoolByName(String name);

    void createDB(String name);

    void initTables(String dbName);
}
