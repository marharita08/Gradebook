package org.example.dao.interfaces;

import org.example.entities.Mark;

import java.util.List;

public interface MarkDAO {
    /**
     * Read mark from database by id.
     * @param id mark id
     * @return Mark
     */
     Mark getMark(int id, String dbName);

    /**
     * Insert new mark into database.
     * @param mark adding mark
     */
    void addMark(Mark mark, String dbName) throws Exception;

    /**
     * Insert absent into database.
     * @param mark adding absent
     */
    void addAbsent(Mark mark, String dbName) throws Exception;

    /**
     * Update mark data into database.
     * @param mark editing mark
     */
    void updateMark(Mark mark, String dbName) throws Exception;

    /**
     * Delete mark from database.
     * @param mark deleted mark
     */
    void deleteMark(Mark mark, String dbName);

    /**
     * Delete mark from database.
     * @param mark deleted absent
     */
   void deleteAbsent(Mark mark, String dbName);

    /**
     * Get marks for pupil with set id.
     * @param id pupil id
     * @return List<Mark>
     */
    List<Mark> getMarksByPupil(int id, String dbName);

    /**
     * Get marks for lesson with set id.
     * @param id lesson id
     * @return List<Mark>
     */
    List<Mark> getMarksByLesson(int id, String dbName);

    /**
     * Get marks for theme and pupil.
     * @param themeID theme id
     * @param pupilID pupil id
     * @return List<Mark>
     */
    List<Mark> getMarksByThemeAndPupil(int themeID, int pupilID, String dbName);

    /**
     * Get semester marks for subject details.
     * @param id subject details id
     * @return List<Mark>
     */
    List<Mark> getSemesterMarks(int id, String dbName);
}
