package org.example.dao.interfaces;

import org.example.entities.Pupil;

import java.util.List;

public interface PupilDAO {
    /**
     * Read all pupils from database and put them into list.
     * @return List<Pupil>
     */
    List<Pupil> getAllPupils();

    /**
     * Read pupil from database by id.
     * @param id pupil id
     * @return Pupil
     */
    Pupil getPupil(int id);

    /**
     * Insert new pupil into database.
     * @param pupil adding pupil
     */
    void addPupil(Pupil pupil);

    /**
     * Update class data into database.
     * @param pupil editing pupil
     */
    void updatePupil(Pupil pupil);

    /**
     * Delete pupil from database.
     * @param id pupil id
     */
    void deletePupil(int id);

    /**
     * Get list of pupils studying in set class.
     * @param id class id
     * @return List<Pupil>
     */
    List<Pupil> getPupilsByPupilClass(int id);

    /**
     * Get total count of pupils from database.
     * @return int
     */
    int getCountOfPupils();

    /**
     * Get pupil list for page.
     * @param page number of page
     * @param range amount of teachers per page
     * @return List<Pupil>
     */
    List<Pupil> getPupilsByPage(int page, int range);

    /**
     * Search pupils by set parameter.
     * @param val text of searching
     * @param param parameter of searching
     * @return List<Pupil>
     * @throws Exception if set parameter is wrong
     */
    List<Pupil> searchPupils(String val, String param) throws Exception;
}
