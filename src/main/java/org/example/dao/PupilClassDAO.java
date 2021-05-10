package org.example.dao;

import org.example.entities.PupilClass;

import java.util.List;

public interface PupilClassDAO {
    /**
     * Read all classes from database and put them into list.
     * @return List<PupilClass>
     */
    List<PupilClass> getAllPupilClasses();

    /**
     * Read class from database by id.
     * @param id class id
     * @return PupilClass
     */
    PupilClass getPupilClass(int id);

    /**
     * Insert new class into database.
     * @param pupilClass adding class
     */
    void addPupilClass(PupilClass pupilClass);

    /**
     * Update class data into database.
     * @param pupilClass editing class
     */
    void updatePupilClass(PupilClass pupilClass);

    /**
     * Delete class from database.
     * @param id class id
     */
    void deletePupilClass(int id);

    /**
     * Read classes from database dy subjects and put them into list.
     * @return List<PupilClass>
     */
    List<PupilClass> getPupilClassesBySubject(int id);
}
