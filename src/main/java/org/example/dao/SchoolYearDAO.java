package org.example.dao;

import org.example.entities.SchoolYear;

import java.util.List;

public interface SchoolYearDAO {
    /**
     * Read all school years from database and put them into list.
     * @return List<SchoolYear>
     */
    List<SchoolYear> getAllSchoolYears();

    /**
     * Read school year from database by id.
     * @param id school year id
     * @return SchoolYear
     */
    SchoolYear getSchoolYear(int id);

    /**
     * Insert new school year into database.
     * @param schoolYear adding school year
     */
    void addSchoolYear(SchoolYear schoolYear);

    /**
     * Update school year data into database.
     * @param schoolYear editing school year
     */
    void updateSchoolYear(SchoolYear schoolYear);

    /**
     * Delete school year from database.
     * @param id school year id
     */
    void deleteSchoolYear(int id);

    /**
     * Get total count of school years from database.
     * @return int
     */
    int getCountOfSchoolYears();

    /**
     * Get school years list for page.
     * @param page number of page
     * @param range amount of school years per page
     * @return List<SchoolYear>
     */
    List<SchoolYear> getSchoolYearsByPage(int page, int range);

    /**
     * Search school years by set parameter.
     * @param val text of searching
     * @param param parameter of searching
     * @return List<SchoolYear>
     * @throws Exception if set parameter is wrong
     */
    List<SchoolYear> searchSchoolYears(String val, String param) throws Exception;
}
