package org.example.dao;

import org.example.entities.Lesson;

import java.util.List;

public interface LessonDAO {

    /**
     * Read lesson from database by id.
     * @param id lesson id
     * @return Pupil
     */
    Lesson getLesson(int id);

    /**
     * Insert new lesson into database.
     * @param lesson adding lesson
     */
    void addLesson(Lesson lesson);

    /**
     * Update lesson data into database.
     * @param lesson editing lesson
     */
    void updateLesson(Lesson lesson);

    /**
     * Delete lesson from database.
     * @param id lesson id
     */
    void deleteLesson(int id);

    /**
     * Read lesson from database by class and subject.
     * @param id subject details id
     * @return List<Lesson>
     */
    List<Lesson> getLessonsBySubjectDetails(int id);

    /**
     * Get count of lessons for set subject details from database.
     * @param id subject details id
     * @return int
     */
    int getCountOfLessons(int id);

    /**
     * Get lesson list for page.
     * @param page number of page
     * @param range amount of lessons per page
     * @param id subject details id
     * @return List<Lesson>
     */
    List<Lesson> getLessonsBySubjectDetailsAndPage(int id, int page, int range);

    /**
     * Search lessons by set parameter and subject details.
     * @param val text of searching
     * @param param parameter of searching
     * @param id subject details id
     * @return List<PupilClass>
     * @throws Exception if set parameter is wrong
     */
    List<Lesson> searchLessons(String val, String param, int id) throws Exception;
}
