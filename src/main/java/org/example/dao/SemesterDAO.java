package org.example.dao;

import org.example.entities.Semester;

import java.util.List;

public interface SemesterDAO {
    /**
     * Read all semesters from database and put them into list.
     * @return List<Semester>
     */
    List<Semester> getAllSemesters();

    /**
     * Read semester from database by id.
     * @param id semester id
     * @return Semester
     */
    Semester getSemester(int id);

    /**
     * Insert new semester into database.
     * @param semester adding semester
     */
   void addSemester(Semester semester);

    /**
     * Update semester data into database.
     * @param semester editing semester
     */
    void updateSemester(Semester semester);

    /**
     * Delete semester from database.
     * @param id semester id
     */
    void deleteSemester(int id);

    /**
     * Get total count of semesters from database.
     * @return int
     */
    int getCountOfSemesters();

    /**
     * Get semesters list for page.
     * @param page number of page
     * @param range amount of semesters per page
     * @return List<Semester>
     */
    List<Semester> getSemestersByPage(int page, int range);

    /**
     * Search semesters by set parameter.
     * @param val text of searching
     * @param param parameter of searching
     * @return List<Semester>
     * @throws Exception if set parameter is wrong
     */
    List<Semester> searchSemesters(String val, String param) throws Exception;
}
