package org.example.dao.interfaces;

import org.example.entities.PupilClass;

import java.util.List;

public interface PupilClassDAO {
    /**
     * Read all classes from database and put them into list.
     * @return List<PupilClass>
     */
    List<PupilClass> getAllPupilClasses(String dbName);

    /**
     * Read class from database by id.
     * @param id class id
     * @return PupilClass
     */
    PupilClass getPupilClass(int id, String dbName);

    /**
     * Insert new class into database.
     * @param pupilClass adding class
     */
    void addPupilClass(PupilClass pupilClass, String dbName);

    /**
     * Update class data into database.
     * @param pupilClass editing class
     */
    void updatePupilClass(PupilClass pupilClass, String dbName);

    /**
     * Delete class from database.
     * @param id class id
     */
    void deletePupilClass(int id, String dbName);

    /**
     * Read classes from database dy subjects and put them into list.
     * @return List<PupilClass>
     */
    List<PupilClass> getPupilClassesBySubject(int id, String dbName);

    /**
     * Get total count of classes from database.
     * @return int
     */
    int getCountOfPupilClasses(String dbName);

    /**
     * Get class list for page.
     * @param page number of page
     * @param range amount of teachers per page
     * @return List<PupilClass>
     */
    List<PupilClass> getPupilClassesByPage(int page, int range, String dbName);

    /**
     * Search classes by set parameter.
     * @param val text of searching
     * @param param parameter of searching
     * @return List<PupilClass>
     * @throws Exception if set parameter is wrong
     */
    List<PupilClass> searchPupilClasses(String val, String param, String dbName) throws Exception;

    /**
     * Read classes from database by teacher and put them into list.
     * @return List<PupilClass>
     */
    List<PupilClass> getPupilClassesByTeacher(int id, String dbName);
}
