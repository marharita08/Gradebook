package org.example.dao;

import org.example.entities.Pupil;
import org.example.entities.PupilClass;

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
}
