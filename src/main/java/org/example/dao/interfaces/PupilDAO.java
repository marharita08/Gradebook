package org.example.dao.interfaces;

import org.example.entities.Pupil;

import java.util.List;

public interface PupilDAO {
    /**
     * Read all pupils from database and put them into list.
     * @return List<Pupil>
     */
    List<Pupil> getAllPupils(String dbName);

    /**
     * Read pupil from database by id.
     * @param id pupil id
     * @return Pupil
     */
    Pupil getPupil(int id, String dbName);

    /**
     * Insert new pupil into database.
     * @param pupil adding pupil
     */
    void addPupil(Pupil pupil, String dbName);

    /**
     * Update class data into database.
     * @param pupil editing pupil
     */
    void updatePupil(Pupil pupil, String dbName);

    /**
     * Delete pupil from database.
     * @param id pupil id
     */
    void deletePupil(int id, String dbName);

    /**
     * Get list of pupils studying in set class.
     * @param id class id
     * @return List<Pupil>
     */
    List<Pupil> getPupilsByPupilClass(int id, String dbName);

    /**
     * Get total count of pupils from database.
     * @return int
     */
    int getCountOfPupils(String dbName);

    /**
     * Get pupil list for page.
     * @param page number of page
     * @param range amount of teachers per page
     * @return List<Pupil>
     */
    List<Pupil> getPupilsByPage(int page, int range, String dbName);

    /**
     * Search pupils by set parameter.
     * @param val text of searching
     * @param param parameter of searching
     * @return List<Pupil>
     * @throws Exception if set parameter is wrong
     */
    List<Pupil> searchPupils(String val, String param, String dbName) throws Exception;
}
