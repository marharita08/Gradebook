package org.example.dao.interfaces;

import org.example.entities.Mark;

import java.util.List;

public interface MarkDAO {
    /**
     * Read mark from database by id.
     * @param id mark id
     * @return Mark
     */
     Mark getMark(int id);

    /**
     * Insert new mark into database.
     * @param mark adding mark
     */
    void addMark(Mark mark) throws Exception;

    /**
     * Update mark data into database.
     * @param mark editing mark
     */
    void updateMark(Mark mark) throws Exception;

    /**
     * Delete mark from database.
     * @param id mark id
     */
    void deleteMark(int id);

    /**
     * Get marks for pupil with set id.
     * @param id pupil id
     * @return List<Mark>
     */
    List<Mark> getMarksByPupil(int id);

    /**
     * Get marks for lesson with set id.
     * @param id lesson id
     * @return List<Mark>
     */
    List<Mark> getMarksByLesson(int id);

    /**
     * Get marks for theme and pupil.
     * @param themeID theme id
     * @param pupilID pupil id
     * @return List<Mark>
     */
    List<Mark> getMarksByThemeAndPupil(int themeID, int pupilID);

    /**
     * Get semester marks for subject details.
     * @param id subject details id
     * @return List<Mark>
     */
    List<Mark> getSemesterMarks(int id);
}
